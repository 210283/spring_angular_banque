import { Component, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountType, OpenAccountRequest, ACCOUNT_TYPE_LABELS } from '../../domain/entities/account.model';

@Component({
  selector: 'app-form-open-account',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <form (ngSubmit)="valid()" class="banque-form">

      <label>
        Account type
        <select [(ngModel)]="accountType" name="accountType">
          @for (type of accountTypes; track type) {
            <option [value]="type">{{ labels[type] }}</option>
          }
        </select>
      </label>

      @if (accountType === 'CURRENT') {
        <label>
          Owner
          <input type="text" [(ngModel)]="owner" name="owner" placeholder="Owner name" />
        </label>
      } @else {
        <label>
          Linked checking account number
          <input type="text" [(ngModel)]="linkedAccountNumber" name="linkedAccountNumber"
                 placeholder="e.g. FR761234567" />
        </label>
        <p class="hint">The owner will be automatically set to the checking account's holder.</p>
      }

      <label>
        Initial deposit
        <input type="number" [(ngModel)]="initialDeposit" name="initialDeposit" placeholder="Initial amount" />
      </label>

      <button type="submit" [disabled]="!isValid() || isSubmitting()">
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
  accountType: AccountType = 'CURRENT';
  linkedAccountNumber = '';

  accountTypes: AccountType[] = ['CURRENT', 'SAVINGS', 'BOOKLET', 'LDD'];
  labels = ACCOUNT_TYPE_LABELS;

  isSubmitting = input<boolean>(false);
  onValidOpenAccount = output<OpenAccountRequest>();

  isValid(): boolean {
    if (this.initialDeposit <= 0) return false;
    if (this.accountType === 'CURRENT') return !!this.owner.trim();
    return !!this.linkedAccountNumber.trim();
  }

  valid() {
    if (this.isValid()) {
      this.onValidOpenAccount.emit({
        owner: this.accountType === 'CURRENT' ? this.owner.trim() : null,
        initialDeposit: this.initialDeposit,
        accountType: this.accountType,
        linkedAccountNumber: this.accountType === 'CURRENT' ? null : this.linkedAccountNumber.trim()
      });
    }
  }
}
