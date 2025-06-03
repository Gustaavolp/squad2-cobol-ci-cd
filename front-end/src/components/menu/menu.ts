import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-menu',
  imports: [],
  templateUrl: './menu.html',
  styleUrl: './menu.scss',
})
export class Menu {
  sidebarActive: boolean = false;
  @Output() newTransaction = new EventEmitter<void>();
  
  toggleSidebar() {
    this.sidebarActive = !this.sidebarActive;
    if(this.sidebarActive) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }
  
  openTransactionModal(event: Event) {
    event.preventDefault();
    this.newTransaction.emit();
    this.toggleSidebar();
  }
}