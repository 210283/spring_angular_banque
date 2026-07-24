import { Component, output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-form-transfer',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="banque-form">
      <input type="number" [(ngModel)]="amount" placeholder="Amount to transfer (ex: 50)" />
      <button (click)="valid()">Confirm transfer</button>
    </div>
  `,
  styleUrl: '../../features/scss/form-transfer.component.scss'
})
export class FormTransferComponent {
  amount: number = 0;

  // The event is emitted to the parent. (la Feature)
  onValidTransfer = output<number>();

  valid() {
    if (this.amount > 0) {
      this.onValidTransfer.emit(this.amount);
    }
  }
}
