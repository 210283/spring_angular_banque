import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { AccountSummaryResponse, ACCOUNT_TYPE_LABELS } from '../../domain/entities/account.model';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-page-summary',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="summary-container">
      <!-- Barre d'onglets secondaire -->
      <nav class="sub-tabs">
        <button
          [class.active]="activeTab() === 'accounts'"
          (click)="activeTab.set('accounts')">
          Mes comptes ({{ summary() ? 1 : 0 }})
        </button>
        <button
          [class.active]="activeTab() === 'savings'"
          (click)="activeTab.set('savings')">
          Mon épargne ({{ savingsAccounts().length }})
        </button>
      </nav>

      @if (loading()) {
        <section class="summary-loading">
          <img src="loading.gif" alt="Loading..." />
        </section>
      }

      @if (errorMessage()) {
        <section class="summary-error">{{ errorMessage() }}</section>
      }

      @if (activeTab() === 'accounts' && !loading()) {
        @if (summary()) {
          <section class="summary-card">
            <p><strong>Account Holder :</strong> {{ summary()?.owner }}</p>
            <p><strong>Account Type :</strong> {{ ACCOUNT_TYPE_LABELS[summary()!.accountType] }}</p>
            <p><strong>Balance :</strong> {{ summary()?.balance | number:'1.2-2' }} €</p>
            @if (summary()?.accountType && summary()?.accountType !== 'CURRENT') {
              <p><strong>Interest Rate :</strong> {{ interestRatePercent() }}</p>
            }
            <div class="actions">
              <button (click)="goToTransfer()">Go to transfer</button>
              <button (click)="logout()">Logout</button>
            </div>
          </section>
        } @else {
          <p class="empty-message">Aucun compte principal disponible.</p>
        }
      }

      @if (activeTab() === 'savings' && !loading()) {
        @if (savingsAccounts().length > 0) {
          <section class="savings-accounts">
            @for (account of savingsAccounts(); track account.accountId) {
              <div class="savings-account-card">
                <p><strong>{{ ACCOUNT_TYPE_LABELS[account.accountType] }}</strong> — {{ account.accountId }}</p>
                <p>Balance : {{ account.balance | number:'1.2-2' }} € — Rate : {{ (account.interestRate * 100).toFixed(2) }} %</p>
              </div>
            }
          </section>
        } @else {
          <p class="empty-message">Aucun compte d'épargne rattaché.</p>
        }
      }
    </div>
  `,
  styleUrls: ['../scss/summary-page.component.scss']
})
export class SummaryPageComponent implements OnInit {
  activeTab = signal<'accounts' | 'savings'>('accounts');
  summary = signal<AccountSummaryResponse | undefined>(undefined);
  savingsAccounts = signal<AccountSummaryResponse[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  readonly ACCOUNT_TYPE_LABELS = ACCOUNT_TYPE_LABELS;

  private router = inject(Router);
  private accountApiService = inject(AccountApiService);
  private authService = inject(AuthService);

  ngOnInit() {
    this.loadMyAccount();
    this.loadSavingsAccounts();
  }

  private loadMyAccount() {
    this.loading.set(true);
    this.errorMessage.set('');
    this.summary.set(undefined);
    this.accountApiService.getMyAccount().subscribe({
      next: (summary) => {
        this.summary.set(summary);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load account summary', err);
        this.errorMessage.set('Unable to load account summary.');
        this.loading.set(false);
      }
    });
  }

  private loadSavingsAccounts() {
    this.accountApiService.getLinkedSavingsAccounts().subscribe({
      next: (accounts) => {
        this.savingsAccounts.set(accounts);
      },
      error: (err) => {
        console.error('Failed to load linked savings accounts', err);
        // Non bloquant : on ne casse pas la page si cet appel échoue
      }
    });
  }

  interestRatePercent(): string {
    const rate = this.summary()?.interestRate;
    return rate ? (rate * 100).toFixed(2) + ' %' : '0.00 %';
  }

  goToTransfer() {
    this.router.navigate(['accounts', 'transfer']);
  }

  logout() {
    this.authService.logout();
  }
}
