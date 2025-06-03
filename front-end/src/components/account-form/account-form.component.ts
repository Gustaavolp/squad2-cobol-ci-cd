import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select'; // Para o tipo de conta
import { MatSnackBar } from '@angular/material/snack-bar';

import { AccountService, AccountType, CreateAccountPayload } from '../account.service';

export interface AccountFormData {
  customerId: string;
}

@Component({
  selector: 'app-account-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDialogModule,
  ],
  templateUrl: './account-form.component.html',
  styleUrls: ['./account-form.component.scss'],
})
export class AccountFormComponent implements OnInit {
  accountForm!: FormGroup;
  accountTypes = Object.values(AccountType); // Para usar no template do select
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private dialogRef: MatDialogRef<AccountFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AccountFormData,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.accountForm = this.fb.group({
      type: ['', Validators.required],
      balance: [0, [Validators.pattern(/^-?\d*\.?\d+$/)]], // Permite números, opcionalmente decimais
      title: [''], // Novo campo, opcional, sem Validators.required
    });
  }

  onSubmit(): void {
    if (this.accountForm.invalid) {
      this.snackBar.open('Por favor, preencha os campos corretamente.', 'Fechar', {
        duration: 3000,
      });
      return;
    }

    this.isLoading = true;

    const formValue = this.accountForm.value;
    const payload: CreateAccountPayload = {
      customerId: this.data.customerId,
      type: formValue.type,
      // Se o saldo não for preenchido ou for inválido, não enviar (backend assume 0)
      // O backend já trata balance null como 0, mas podemos ser explícitos ou apenas não enviar se for zero/vazio
      balance: formValue.balance ? parseFloat(formValue.balance) : undefined,
      title: formValue.title || undefined, // Envia title, ou undefined se for string vazia
    };

    this.accountService.createAccount(payload).subscribe({
      next: newAccount => {
        this.isLoading = false;
        this.snackBar.open('Conta criada com sucesso!', 'Fechar', {
          duration: 3000,
          panelClass: 'success-snackbar',
        });
        this.dialogRef.close(true); // Fecha o dialog e sinaliza sucesso
      },
      error: error => {
        this.isLoading = false;
        const errorMessage = error.message || 'Erro ao criar conta. Tente novamente.';
        this.snackBar.open(errorMessage, 'Fechar', {
          duration: 5000,
          panelClass: 'error-snackbar',
        });
        // Não fecha o dialog em caso de erro, para o usuário poder tentar novamente ou corrigir.
      },
    });
  }

  onCancel(): void {
    this.dialogRef.close(false); // Fecha o dialog sem sinalizar sucesso
  }
}
