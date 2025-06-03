import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransactionModalService {
  private visibleSubject = new BehaviorSubject<boolean>(false);
  
  public get visible$(): Observable<boolean> {
    return this.visibleSubject.asObservable();
  }

  public get isVisible(): boolean {
    return this.visibleSubject.getValue();
  }

  public open(): void {
    this.visibleSubject.next(true);
  }

  public close(): void {
    this.visibleSubject.next(false);
  }
}