import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-transaction-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transaction-modal.component.html',
  styleUrl: './transaction-modal.component.scss'
})
export class TransactionModalComponent {
  @Input() visible: boolean = false;
  @Input() users: any[] = [];
  @Input() accounts: any[] = [];
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<any>();

  transaction = {
    userId: '',
    accountId: '',
    amount: 0,
    description: '',
    type: 'debit' // 'debit' ou 'credit'
  };

  userAccounts: any[] = [];

  onUserChange() {
    this.transaction.accountId = '';
    this.userAccounts = this.accounts.filter(account => 
      account.userId === this.transaction.userId
    );
  }

  onSubmit() {
    if (this.transaction.type === 'debit') {
      this.transaction.amount = -Math.abs(this.transaction.amount);
    } else {
      this.transaction.amount = Math.abs(this.transaction.amount);
    }
    
    this.submit.emit(this.transaction);
    this.resetForm();
  }

  onClose() {
    this.close.emit();
    this.resetForm();
  }

  private resetForm() {
    this.transaction = {
      userId: '',
      accountId: '',
      amount: 0,
      description: '',
      type: 'debit'
    };
    this.userAccounts = [];
  }
}