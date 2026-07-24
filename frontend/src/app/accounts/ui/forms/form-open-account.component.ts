import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OpenAccountRequest } from '../../domain/entities/account.model';

@Component({
  selector: 'app-form-open-account',
  standalone: true,
  imports: [FormsModule],
  template: `
    <form (ngSubmit)="valid()" class="banque-form">
      <label>
        Owner
        <input type="text" [(ngModel)]="owner" name="owner" placeholder="Owner name" />
      </label>

      <label>
        Initial deposit
        <input type="number" [(ngModel)]="initialDeposit" name="initialDeposit" placeholder="Initial amount" />
      </label>

      <button type="submit" [disabled]="!owner.trim() || initialDeposit <= 0 || isSubmitting()">
        @if (isSubmitting()) {
          <img src="loading.gif" alt="Loading..." />
          Opening Account...
        } @else {
          Open Account
        }
      </button>
    </form>
  `
})
export class FormOpenAccountComponent {
  owner = '';
  initialDeposit = 0;

  isSubmitting = input<boolean>(false);

  onValidOpenAccount = output<OpenAccountRequest>();

  valid() {
    if (this.owner.trim() && this.initialDeposit > 0) {
      this.onValidOpenAccount.emit({
        owner: this.owner.trim(),
        initialDeposit: this.initialDeposit,
      });
    }
  }
}
