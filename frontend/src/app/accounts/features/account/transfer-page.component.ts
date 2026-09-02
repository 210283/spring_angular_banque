import { Component, OnInit, signal } from '@angular/core';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { FormTransferComponent } from '../../ui/forms/form-transfer.component';
import { BeneficiaryResponse, TransferRequest, ACCOUNT_TYPE_LABELS } from '../../domain/entities/account.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-page-transfer',
  standalone: true,
  imports: [FormTransferComponent],
  template: `
    <div class="transfer-container">
      <h2>Transfer operation</h2>

      @if (loading()) {
        <section class="summary-loading"><img src="loading.gif" alt="Loading..." /></section>
      }

      @if (errorMessage()) {
        <section class="summary-error">{{ errorMessage() }}</section>
      }

      @if (!loading()){
        <ng-container>
          <button class="btn-secondary" (click)="goToAddBeneficiary()" [disabled]="isSubmitting()">Add beneficiary</button>

          @if (!selectedBeneficiary) {
            <div class="beneficiary-selection">
              <label for="beneficiary-select">Choose a beneficiary :</label>
              <select id="beneficiary-select" (change)="onSelectBeneficiary($event)">
                <option value="">-- Please choose --</option>
                @for (beneficiary of beneficiaries(); track beneficiary.beneficiaryAccountNumber) {
                  <option [value]="beneficiary.beneficiaryAccountNumber">
                    {{ beneficiary.accountName }} — {{ ACCOUNT_TYPE_LABELS[beneficiary.accountType] }} ({{ beneficiary.beneficiaryAccountNumber }})
                  </option>
                }
              </select>
            </div>
          } @else {
            <div class="selected-info">
              <p>Selected beneficiary : <strong>{{ selectedBeneficiary.accountName }}</strong></p>
              <p><small>Account: {{ selectedBeneficiary.beneficiaryAccountNumber }}</small></p>
              <button class="btn-secondary" (click)="selectedBeneficiary = null" [disabled]="isSubmitting()">Change beneficiary</button>
            </div>

            <hr />

            <app-form-transfer [isSubmitting]="isSubmitting()" (onValidTransfer)="makeTransfer($event)"></app-form-transfer>
          }
        </ng-container>
      }
    </div>
  `,
  styleUrl: '../scss/transfer-page.component.scss'
})
export class TransferPageComponent implements OnInit {
  errorMessage = signal('');
  loading = signal(false);
  isSubmitting = signal(false);
  beneficiaries = signal<BeneficiaryResponse[]>([]);
  readonly ACCOUNT_TYPE_LABELS = ACCOUNT_TYPE_LABELS;

  private sourceAccountNumber = '';
  selectedBeneficiary: BeneficiaryResponse | null = null;

  constructor(
    private router: Router,
    private accountApiService: AccountApiService
  ) {}

  ngOnInit(): void {
    this.loading.set(true);

    this.accountApiService.getMyAccount().subscribe({
      next: (summary) => {
        this.sourceAccountNumber = summary.accountId;
        this.loading.set(false);
        this.getBeneficiaries(this.sourceAccountNumber);
      },
      error: () => {
        this.errorMessage.set('Unable to load your account.');
        this.loading.set(false);
      }
    });
  }

  onSelectBeneficiary(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const accountNumber = selectElement.value;

    this.selectedBeneficiary = this.beneficiaries().find(b => b.beneficiaryAccountNumber === accountNumber) || null;
  }

  makeTransfer(amountToTransfer: number) {
    if (!this.selectedBeneficiary) return;

    this.isSubmitting.set(true);

    const payload: TransferRequest = {
      sourceAccountNumber: this.sourceAccountNumber,
      destinationAccountNumber: this.selectedBeneficiary.beneficiaryAccountNumber,
      amount: amountToTransfer
    };

    this.accountApiService.transfer(payload).subscribe({
      next: (reponseMessage) => {
        alert(reponseMessage);
        this.isSubmitting.set(false);
        this.selectedBeneficiary = null;
        this.router.navigate(['accounts', 'summary']);
      },
      error: (err) => {
        this.isSubmitting.set(false);

        let messageErreur = 'Transfer failed.';
        const parsedError = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        if (parsedError?.detail) {
          messageErreur = parsedError.detail;
        }

        alert(messageErreur);
        this.errorMessage.set('Transfer failed.');
      }
    });
  }

  getBeneficiaries(accountNumber: string) {
    this.accountApiService.getBeneficiaries(accountNumber).subscribe({
      next: (beneficiaries) => {
        this.beneficiaries.set(beneficiaries);
      },
      error: () => {
        this.errorMessage.set('Unable to load beneficiaries.');
      }
    });
  }

  goToAddBeneficiary() {
    this.router.navigate(['accounts', 'add-beneficiary']);
  }
}
