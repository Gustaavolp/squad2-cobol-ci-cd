import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';

import { CustomerService, Customer } from '../customer.service';
import { AccountService, Account } from '../account.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Menu } from '../menu/menu';
import { AccountFormComponent } from '../account-form/account-form.component';
import { TransactionFormComponent } from '../transaction-form/transaction-form.component';
import { TransactionType } from '../transaction.service';

@Component({
  selector: 'app-list-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatSnackBarModule,
    Menu,
  ],
  templateUrl: './list-page.html',
  styleUrls: ['./list-page.scss'],
})
export class ListPage implements OnInit, OnDestroy {
  displayedColumns: string[] = ['name', 'email', 'birthDate', 'actions'];
  dataSource = new MatTableDataSource<Customer>([]);

  displayedAccountColumns: string[] = ['id', 'title', 'type', 'balance', 'actions'];
  accountDataSource = new MatTableDataSource<Account>([]);

  selectedCustomerForAccounts: Customer | null = null;
  showingAccounts = false;

  private subscriptions = new Subscription();

  @ViewChild('customerPaginator') customerPaginator!: MatPaginator;
  @ViewChild('customerSort') customerSort!: MatSort;
  @ViewChild('accountPaginator') accountPaginator!: MatPaginator;
  @ViewChild('accountSort') accountSort!: MatSort;

  constructor(
    private customerService: CustomerService,
    private accountService: AccountService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadCustomers(): void {
    const sub = this.customerService.getCustomers().subscribe({
      next: (data: Customer[]) => {
        this.snackBar.dismiss();
        this.dataSource.data = data;
        if (this.customerPaginator) this.dataSource.paginator = this.customerPaginator;
        if (this.customerSort) this.dataSource.sort = this.customerSort;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error loading customers:', err);
        // this.showErrorMessage('Erro ao carregar clientes. Verifique o console.');
      },
    });
    this.subscriptions.add(sub);
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  viewAccounts(customer: Customer): void {
    this.selectedCustomerForAccounts = customer;
    this.showingAccounts = true;
    if (customer.id) {
      const sub = this.accountService.getAccountsByCustomerId(customer.id).subscribe({
        next: (accounts: Account[]) => {
          this.accountDataSource.data = accounts;
          if (this.accountPaginator) this.accountDataSource.paginator = this.accountPaginator;
          if (this.accountSort) this.accountDataSource.sort = this.accountSort;
        },
        error: (err: HttpErrorResponse) => {
          console.error(`Error loading accounts for customer ${customer.id}:`, err);
          this.showErrorMessage(`Erro ao carregar contas para ${customer.name}.`);
        },
      });
      this.subscriptions.add(sub);
    } else {
      console.warn('Selected customer has no ID, cannot fetch accounts.');
      this.showErrorMessage('Cliente selecionado não possui ID para buscar contas.');
    }
  }

  closeAccountsView(): void {
    this.showingAccounts = false;
    this.selectedCustomerForAccounts = null;
    this.accountDataSource.data = [];
  }

  deleteCustomer(id: string, name: string): void {
    const confirmation = window.confirm(
      `Tem certeza que deseja excluir o cliente ${name}? Esta ação não pode ser desfeita.`
    );

    if (confirmation) {
      const deleteSub = this.customerService.deleteCustomer(id).subscribe({
        next: () => {
          this.showSuccessMessage('Cliente excluído com sucesso!');
          this.loadCustomers();
          if (this.selectedCustomerForAccounts?.id === id) {
            this.closeAccountsView();
          }
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error deleting customer:', err);
          const errMsg = err.error?.message || err.message || 'Erro ao excluir cliente.';
          this.showErrorMessage(errMsg);
        },
      });
      this.subscriptions.add(deleteSub);
    }
  }

  openAccountDialog(customerId: string | undefined | null): void {
    if (!customerId) {
      this.showErrorMessage('ID do cliente não fornecido para criar conta.');
      return;
    }

    const dialogRef = this.dialog.open(AccountFormComponent, {
      width: '400px',
      data: { customerId: customerId },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        if (this.selectedCustomerForAccounts) {
          this.viewAccounts(this.selectedCustomerForAccounts);
        } else {
          this.showInfoMessage(
            'Nova conta criada. Atualize a visualização de contas se necessário.'
          );
        }
        this.showSuccessMessage('Operação de conta bem-sucedida!');
      }
    });
  }

  deleteAccount(accountId: string): void {
    if (!this.selectedCustomerForAccounts || !this.selectedCustomerForAccounts.id) {
      this.showErrorMessage('Cliente não selecionado para excluir a conta.');
      return;
    }
    const confirmation = window.confirm(
      `Tem certeza que deseja excluir a conta ID: ${accountId}? Esta ação não pode ser desfeita.`
    );
    if (confirmation) {
      const deleteAccSub = this.accountService.deleteAccount(accountId).subscribe({
        next: () => {
          this.showSuccessMessage('Conta excluída com sucesso!');
          if (this.selectedCustomerForAccounts) {
            this.viewAccounts(this.selectedCustomerForAccounts);
          }
        },
        error: (err: HttpErrorResponse) => {
          console.error('Error deleting account:', err);
          const errMsg = err.error?.message || err.message || 'Erro ao excluir conta.';
          this.showErrorMessage(errMsg);
        },
      });
      this.subscriptions.add(deleteAccSub);
    }
  }

  addCustomer(): void {
    console.log('Navegação para novo cliente via routerLink.');
  }

  editCustomer(customer: Customer): void {
    console.log('Navegação para editar cliente via routerLink.');
  }

  manageAccounts(customer: Customer): void {
    console.log('Gerenciar contas para:', customer.name);
    this.viewAccounts(customer);
  }

  openTransactionDialog(account: Account, transactionType: TransactionType): void {
    if (!account || !account.id) {
      this.showErrorMessage('Detalhes da conta inválidos.');
      return;
    }

    const dialogRef = this.dialog.open(TransactionFormComponent, {
      width: '400px',
      data: {
        accountId: account.id,
        transactionType: transactionType,
        accountTitle: account.title || `Conta ID ${account.id}`,
      },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        if (this.selectedCustomerForAccounts) {
          this.viewAccounts(this.selectedCustomerForAccounts);
        }
      }
    });
  }

  deposit(account: Account): void {
    this.openTransactionDialog(account, TransactionType.DEPOSIT);
  }

  withdraw(account: Account): void {
    this.openTransactionDialog(account, TransactionType.WITHDRAWAL);
  }

  private showSuccessMessage(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: 'success-snackbar',
    });
  }

  private showErrorMessage(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: 'error-snackbar',
    });
  }

  private showInfoMessage(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: 'info-snackbar',
    });
  }
}
