import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AccountUseCases } from '../../application/account.use-cases';
import { ACCOUNT_USE_CASES } from '../../../app.config';
import { Transaction } from '../../domain/models/account.model';
import { Router } from '@angular/router';
import { from } from 'rxjs';

@Component({
  selector: 'app-transaction-history-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="transactions-container">
      <h2>Transaction History</h2>

      @if (loading()) {
        <section class="summary-loading"><img src="loading.gif" alt="Loading..." /></section>
      }
      @if (errorMessage()) {
        <section class="summary-error">{{ errorMessage() }}</section>
      }

      @if (!loading() && !errorMessage()) {
        @if (transactions().length === 0) {
          <p>No transactions yet.</p>
        } @else {
          <table class="transactions-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Label</th>
                <th>Counterparty</th>
                <th>Amount</th>
                <th>Balance after</th>
              </tr>
            </thead>
            <tbody>
              @for (tx of transactions(); track tx.id) {
                <tr [class.debit]="tx.type === 'DEBIT'" [class.credit]="tx.type === 'CREDIT'">
                  <td>{{ tx.occurredAt | date:'short' }}</td>
                  <td>{{ tx.label }}</td>
                  <td>{{ tx.counterpartyName || '—' }}</td>
                  <td>{{ tx.type === 'DEBIT' ? '-' : '+' }}{{ tx.amount | number:'1.2-2' }} €</td>
                  <td>{{ tx.balanceAfter | number:'1.2-2' }} €</td>
                </tr>
              }
            </tbody>
          </table>
        }
      }

      <button (click)="goBack()">Back to summary</button>
    </div>
  `,
  styleUrl: '../scss/transaction-history-page.component.scss'
})
export class TransactionHistoryPageComponent implements OnInit {
  private accountApiService = inject(ACCOUNT_USE_CASES);
  private router = inject(Router);

  transactions = signal<Transaction[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  ngOnInit(): void {
    this.loading.set(true);

    from(this.accountApiService.getMyAccount()).subscribe({
      next: (summary) => {
        from(this.accountApiService.getTransactionHistory(summary.accountId)).subscribe({
          next: (transactions) => {
            this.transactions.set(transactions);
            this.loading.set(false);
          },
          error: () => {
            this.errorMessage.set('Unable to load transaction history.');
            this.loading.set(false);
          }
        });
      },
      error: () => {
        this.errorMessage.set('Unable to load your account.');
        this.loading.set(false);
      }
    });
  }

  goBack() {
    this.router.navigate(['accounts', 'summary']);
  }
}
