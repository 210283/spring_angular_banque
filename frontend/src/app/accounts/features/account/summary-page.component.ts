import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { AccountSummaryResponse } from '../../domain/entities/account.model';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-page-summary',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="summary-container">
      <h2>Account Summary</h2>

      @if (loading()) {
        <section class="summary-loading"><img src="loading.gif" alt="Loading..." /></section>
      }

      @if (errorMessage()) {
        <section class="summary-error">{{ errorMessage() }}</section>
      }

      @if (summary()) {
        <section class="summary-card">
          <p><strong>Account Holder :</strong> {{ summary()?.owner }}</p>
          <p><strong>Balance :</strong> {{ summary()?.balance | number:'1.2-2' }} €</p>
          <button (click)="goToTransfer()">Go to transfer</button>
          <button (click)="logout()">Logout</button>
        </section>
      }
    </div>
  `,
  styleUrls: ['../scss/summary-page.component.scss']
})
export class SummaryPageComponent implements OnInit {
  summary = signal<AccountSummaryResponse | undefined>(undefined);
  loading = signal(false);
  errorMessage = signal('');

  private router = inject(Router);
  private accountApiService = inject(AccountApiService);
  private authService = inject(AuthService);

  ngOnInit() {
    this.loadMyAccount();
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

  goToTransfer() {
    this.router.navigate(['accounts', 'transfer']);
  }

  logout(){
    this.authService.logout();
  }
}
