import { Directive, ElementRef, HostListener, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appDateFormat]',
  standalone: true
})
export class DateFormatDirective {
  constructor(private el: ElementRef, private renderer: Renderer2) {}

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
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
    
    this.renderer.setProperty(input, 'value', value);
  }
}