import {
  Component,
  forwardRef,
  Input,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { MatNativeDateModule } from '@angular/material/core';

@Component({
  selector: 'app-date-input',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  template: `
    <mat-form-field appearance="outline" class="full-width">
      <mat-label>{{ label }}</mat-label>
      <input
        #inputElement
        matInput
        [matDatepicker]="picker"
        [placeholder]="placeholder"
        [disabled]="disabled"
        [value]="value"
        (dateChange)="onDateChange($event.value)"
        (blur)="markAsTouched()"
        (focus)="onFocus()"
      />
      <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
      <mat-datepicker #picker></mat-datepicker>
    </mat-form-field>
  `,
  styles: [
    `
      .full-width {
        width: 100%;
      }
    `,
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DateInputComponent),
      multi: true,
    },
  ],
})
export class DateInputComponent implements ControlValueAccessor, AfterViewInit {
  @Input() label = 'Data';
  @Input() placeholder = 'DD/MM/AAAA';
  @ViewChild('inputElement') inputElement!: ElementRef<HTMLInputElement>;

  value: Date | null = null;
  disabled = false;
  touched = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  ngAfterViewInit(): void {
    // Adiciona um pequeno atraso para garantir que o DOM esteja estável
    setTimeout(() => {
      this.updateInputValue();
    }, 100);
  }

  onDateChange(date: Date): void {
    this.value = date;
    this.onChange(date);
    this.markAsTouched();
  }

  onFocus(): void {
    // Quando o campo recebe foco, não fazemos nada especial
  }

  markAsTouched(): void {
    if (!this.touched) {
      this.touched = true;
      this.onTouched();
    }
  }

  writeValue(value: any): void {
    if (value) {
      this.value = new Date(value);

      // Se o componente já foi inicializado, atualiza o valor do input
      if (this.inputElement) {
        this.updateInputValue();
      }
    } else {
      this.value = null;
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  private updateInputValue(): void {
    if (this.value && this.inputElement) {
      const day = this.value.getDate().toString().padStart(2, '0');
      const month = (this.value.getMonth() + 1).toString().padStart(2, '0');
      const year = this.value.getFullYear();

      // Atualiza o valor do input diretamente
      this.inputElement.nativeElement.value = `${day}/${month}/${year}`;
    }
  }
}
