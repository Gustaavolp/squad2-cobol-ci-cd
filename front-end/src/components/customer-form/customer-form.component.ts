import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CustomerService, Customer } from '../customer.service';
import { ageValidator } from './validators/age.validator';
import { NgxMaskDirective, NgxMaskPipe, provideNgxMask } from 'ngx-mask';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClientModule } from '@angular/common/http';
import { DateInputMaskDirective } from './date-input-mask.directive';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-customer-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    NgxMaskDirective,
    NgxMaskPipe,
    RouterModule,
    MatSnackBarModule,
    HttpClientModule,
    DateInputMaskDirective,
    MatSelectModule,
  ],
  providers: [provideNgxMask()],
  templateUrl: './customer-form.component.html',
  styleUrls: ['./customer-form.component.scss'],
})
// Componente para o formulário de cliente
export class CustomerFormComponent implements OnInit {
  customerForm!: FormGroup;
  isUpdateMode = false;
  customerId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();

    this.customerId = this.route.snapshot.paramMap.get('id');
    if (this.customerId) {
      this.isUpdateMode = true;
      // this.loadCustomerData(this.customerId); // Manter comentado por enquanto, focar na criação
    }
  }

  initForm(): void {
    this.customerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      birthdate: ['', [Validators.required, ageValidator(18)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['USER', [Validators.required]],
    });
  }

  // loadCustomerData(id: string): void { // Manter comentado por enquanto
  //   this.customerService.getCustomerById(id).subscribe({
  //     next: (customer) => {
  //       const birthDate = customer.birthdate ? new Date(customer.birthdate) : null;

  //       this.customerForm.patchValue({
  //         name: customer.name,
  //         birthdate: birthDate,
  //         email: customer.email,
  //         role: customer.role
  //       });
  //     },
  //     error: (error) => {
  //       console.error('Error loading customer data', error);
  //       this.showErrorMessage('Erro ao carregar dados do cliente');
  //     }
  //   });
  // }

  onSubmit(): void {
    if (this.customerForm.invalid) {
      this.customerForm.markAllAsTouched();
      return;
    }

    const customerData = this.customerForm.value as Omit<Customer, 'id'>;
    // console.log('Customer data:', customerData); // Já verificado

    if (this.isUpdateMode && this.customerId) {
      // Lógica de atualização será implementada depois
      // this.customerService.updateCustomer(this.customerId, customerData).subscribe({ ... });
      this.customerService
        .updateCustomer(this.customerId, { ...customerData, id: this.customerId })
        .subscribe({
          next: response => {
            console.log('Customer updated successfully', response);
            this.showSuccessMessage('Cliente atualizado com sucesso!');
            this.router.navigate(['/customers']);
          },
          error: (error: Error) => {
            console.error('Error updating customer', error);
            this.showErrorMessage(error.message);
          },
        });
    } else {
      // Criação de novo cliente
      this.customerService.createCustomer(customerData).subscribe({
        next: response => {
          console.log('Customer created successfully', response);
          this.showSuccessMessage('Cliente cadastrado com sucesso!');
          this.router.navigate(['/customers']); // Ajustar para a rota correta da lista de clientes, se diferente
        },
        error: (error: Error) => {
          console.error('Error creating customer', error);
          this.showErrorMessage(error.message);
        },
      });
    }
  }

  getErrorMessage(controlName: string): string {
    const control = this.customerForm.get(controlName);

    if (!control || !control.errors || !control.touched) {
      return '';
    }

    if (control.errors['required']) {
      return 'Campo obrigatório';
    }

    if (controlName === 'name' && control.errors['minlength']) {
      return 'Nome deve ter pelo menos 3 caracteres';
    }

    if (controlName === 'birthdate') {
      if (control.errors['underage']) {
        const { requiredAge, actualAge } = control.errors['underage'];
        return `Cliente deve ter pelo menos ${requiredAge} anos. Idade atual: ${actualAge}`;
      }
      if (control.errors['invalidDate']) {
        return 'Formato de data inválido.';
      }
    }

    if (controlName === 'email' && control.errors['email']) {
      return 'Email inválido. Formato esperado: exemplo@dominio.com';
    }

    if (controlName === 'password' && control.errors['minlength']) {
      return `Senha deve ter pelo menos ${control.errors['minlength'].requiredLength} caracteres`;
    }

    return 'Campo inválido';
  }

  private showSuccessMessage(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['success-snackbar'],
    });
  }

  private showErrorMessage(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 5000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'],
    });
  }
}
