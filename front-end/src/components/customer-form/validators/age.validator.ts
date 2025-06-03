import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function ageValidator(minAge: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null; // Não valida se o campo estiver vazio (required deve cuidar disso)
    }

    let birthDate: Date;

    // Adicionado console.log para depuração
    console.log('ageValidator - control.value:', control.value);

    if (typeof control.value === 'string') {
      // Tenta converter de DD/MM/AAAA para um objeto Date
      const parts = control.value.split('/');
      if (parts.length === 3) {
        const day = parseInt(parts[0], 10);
        const month = parseInt(parts[1], 10) - 1; // Mês é 0-indexado no objeto Date
        const year = parseInt(parts[2], 10);
        birthDate = new Date(year, month, day);
      } else {
        // Se o formato não for DD/MM/AAAA, tenta o parse padrão, mas pode ser arriscado
        birthDate = new Date(control.value);
      }
    } else if (control.value instanceof Date) {
      birthDate = control.value;
    } else {
      // Valor inesperado, considera inválido ou retorna um erro específico
      return { invalidDate: true };
    }

    // Adicionado console.log para depuração
    console.log('ageValidator - parsed birthDate:', birthDate);

    if (isNaN(birthDate.getTime())) {
      // Data inválida após o parse
      return { invalidDate: true };
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0); // Zera horas para comparar apenas datas
    birthDate.setHours(0, 0, 0, 0); // Zera horas para comparar apenas datas

    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    console.log('ageValidator - calculated age:', age);

    return age < minAge ? { underage: { requiredAge: minAge, actualAge: age } } : null;
  };
}
