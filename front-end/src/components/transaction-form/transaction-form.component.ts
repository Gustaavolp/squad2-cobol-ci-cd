import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';

import {
  TransactionService,
  TransactionType,
  CreateTransactionPayload,
} from '../transaction.service';

export interface TransactionFormData {
  accountId: string;
  transactionType: TransactionType; // DEPOSIT ou WITHDRAWAL
  accountTitle?: string; // Para exibir no título do dialog
}

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
  ],
  templateUrl: './transaction-form.component.html',
  styleUrls: ['./transaction-form.component.scss'],
})
export class TransactionFormComponent implements OnInit {
  transactionForm!: FormGroup;
  isLoading = false;
  title = '';

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private dialogRef: MatDialogRef<TransactionFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TransactionFormData,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.title =
      this.data.transactionType === TransactionType.DEPOSIT
        ? 'Realizar Depósito'
        : 'Realizar Retirada';
    if (this.data.accountTitle) {
      this.title += ` em ${this.data.accountTitle}`;
    }

    this.transactionForm = this.fb.group({
      amount: ['', [Validators.required, Validators.pattern(/^\d*\.?\d+$/), Validators.min(0.01)]],
    });
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) {
      this.snackBar.open('Por favor, insira um valor válido.', 'Fechar', { duration: 3000 });
      return;
    }

    this.isLoading = true;
    const formValue = this.transactionForm.value;

    const payload: CreateTransactionPayload = {
      accountId: this.data.accountId,
      type: this.data.transactionType,
      amount: parseFloat(formValue.amount),
    };

    this.transactionService.createTransaction(payload).subscribe({
      next: () => {
        this.isLoading = false;
        const successMessage =
          this.data.transactionType === TransactionType.DEPOSIT
            ? 'Depósito realizado com sucesso!'
            : 'Retirada realizada com sucesso!';
        this.snackBar.open(successMessage, 'Fechar', {
          duration: 3000,
          panelClass: 'success-snackbar',
        });
        this.dialogRef.close(true); // Fecha o dialog e sinaliza sucesso
      },
      error: error => {
        this.isLoading = false;
        const errorMessage = error.message || 'Erro ao processar transação. Tente novamente.';
        this.snackBar.open(errorMessage, 'Fechar', {
          duration: 5000,
          panelClass: 'error-snackbar',
        });
      },
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
