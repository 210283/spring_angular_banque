import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { BeneficiaryResponse, DirectDebitFrequency, DirectDebitResponse } from '../../domain/entities/account.model';
import { Router } from '@angular/router';
import { ACCOUNT_TYPE_LABELS } from '../../domain/entities/account.model';

@Component({
  selector: 'app-direct-debits-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="direct-debits-container">
      <h2>Direct debits</h2>

      @if (loading()) {
        <section class="summary-loading"><img src="loading.gif" alt="Loading..." /></section>
      }
      @if (errorMessage()) {
        <section class="summary-error">{{ errorMessage() }}</section>
      }

      @if (!loading()) {
        <section class="direct-debit-form">
          <h3>Set up a new direct debit</h3>
          <label>
            Beneficiary
            <select [(ngModel)]="selectedBeneficiary">
              <option [ngValue]="null">-- Please choose --</option>
              @for (b of beneficiaries(); track b.beneficiaryAccountNumber) {
                <option [ngValue]="b">{{ b.accountName }} — {{ ACCOUNT_TYPE_LABELS[b.accountType] }} ({{ b.beneficiaryAccountNumber }})</option>
              }
            </select>
          </label>
          <label>
            Amount
            <input type="number" [(ngModel)]="amount" placeholder="e.g. 50" />
          </label>
          <label>
            Frequency
            <select [(ngModel)]="frequency">
              <option value="MONTHLY">Monthly</option>
              <option value="WEEKLY">Weekly</option>
            </select>
          </label>
          <label>
            Start date
            <input type="date" [(ngModel)]="startDate" />
          </label>
          <button (click)="create()" [disabled]="!isValid() || isSubmitting()">
            {{ isSubmitting() ? 'Creating...' : 'Create direct debit' }}
          </button>
        </section>

        <section class="direct-debit-list">
          <h3>Active direct debits</h3>
          @if (directDebits().length === 0) {
            <p>No direct debits set up.</p>
          } @else {
            @for (dd of directDebits(); track dd.id) {
              <div class="direct-debit-card">
                <p>{{ dd.beneficiaryLabel }} ({{ dd.beneficiaryAccountNumber }})</p>
                <p>{{ dd.amount | number:'1.2-2' }} € — {{ dd.frequency }} — next: {{ dd.nextExecutionDate }}</p>
                @if (dd.active) {
                  <button (click)="cancel(dd.id)">Cancel</button>
                } @else {
                  <span class="inactive-label">Cancelled</span>
                }
              </div>
            }
          }
        </section>
      }

      <button (click)="goBack()">Back to transfer</button>
    </div>
  `,
  styleUrl: '../scss/direct-debits-page.component.scss'
})
export class DirectDebitsPageComponent implements OnInit {
  private accountApiService = inject(AccountApiService);
  private router = inject(Router);
  readonly ACCOUNT_TYPE_LABELS = ACCOUNT_TYPE_LABELS;

  loading = signal(false);
  errorMessage = signal('');
  isSubmitting = signal(false);

  private accountNumber = '';
  beneficiaries = signal<BeneficiaryResponse[]>([]);
  directDebits = signal<DirectDebitResponse[]>([]);

  selectedBeneficiary: BeneficiaryResponse | null = null;
  amount = 0;
  frequency: DirectDebitFrequency = 'MONTHLY';
  startDate = new Date().toISOString().slice(0, 10);

  ngOnInit(): void {
    this.loading.set(true);

    this.accountApiService.getMyAccount().subscribe({
      next: (summary) => {
        this.accountNumber = summary.accountId;
        this.loadBeneficiaries();
        this.loadDirectDebits();
      },
      error: () => {
        this.errorMessage.set('Unable to load your account.');
        this.loading.set(false);
      }
    });
  }

  private loadBeneficiaries() {
    this.accountApiService.getBeneficiaries(this.accountNumber).subscribe({
      next: (beneficiaries) => {
        this.beneficiaries.set(beneficiaries);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load beneficiaries.');
        this.loading.set(false);
      }
    });
  }

  private loadDirectDebits() {
    this.accountApiService.getDirectDebits(this.accountNumber).subscribe({
      next: (dds) => this.directDebits.set(dds),
      error: () => this.errorMessage.set('Unable to load direct debits.')
    });
  }

  isValid(): boolean {
    return !!this.selectedBeneficiary && this.amount > 0 && !!this.startDate;
  }

  create() {
    if (!this.isValid() || !this.selectedBeneficiary) return;

    this.isSubmitting.set(true);

    this.accountApiService.createDirectDebit(this.accountNumber, {
      beneficiaryAccountNumber: this.selectedBeneficiary.beneficiaryAccountNumber,
      amount: this.amount,
      frequency: this.frequency,
      startDate: this.startDate
    }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.amount = 0;
        this.selectedBeneficiary = null;
        this.loadDirectDebits();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        alert(err.error?.detail || 'Unable to create direct debit.');
      }
    });
  }

  cancel(directDebitId: string) {
    this.accountApiService.cancelDirectDebit(this.accountNumber, directDebitId).subscribe({
      next: () => this.loadDirectDebits(),
      error: () => alert('Unable to cancel direct debit.')
    });
  }

  goBack() {
    this.router.navigate(['accounts', 'transfer']);
  }
}
