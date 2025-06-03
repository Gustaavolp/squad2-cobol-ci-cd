import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appDateInputMask]',
  standalone: true
})
export class DateInputMaskDirective {
  constructor(private el: ElementRef) {}

  @HostListener('input', ['$event'])
  onInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');
    
    if (value.length > 8) {
      value = value.substring(0, 8);
    }
    
    if (value.length > 4) {
      value = `${value.substring(0, 2)}/${value.substring(2, 4)}/${value.substring(4)}`;
    } else if (value.length > 2) {
      value = `${value.substring(0, 2)}/${value.substring(2)}`;
    }
    
    input.value = value;
  }
}