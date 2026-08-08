import { Component, output, input } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-form-transfer',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="banque-form">
      <input type="number" [(ngModel)]="amount" placeholder="Amount to transfer (ex: 50)" [disabled]="isSubmitting()" />
      <button (click)="valid()" [disabled]="isSubmitting()">
        {{ isSubmitting() ? 'Processing...' : 'Confirm transfer' }}
      </button>
    </div>
  `,
  styleUrl: '../../features/scss/form-transfer.component.scss'
})
export class FormTransferComponent {
  amount: number = 0;

  isSubmitting = input<boolean>(false);
  onValidTransfer = output<number>();

  valid() {
    if (this.amount > 0) {
      this.onValidTransfer.emit(this.amount);
    }
  }
}
