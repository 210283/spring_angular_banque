import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormOpenAccountComponent } from '../../ui/forms/form-open-account.component';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { OpenAccountRequest, AccountCreationResponse } from '../../domain/entities/account.model';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-page-open-account',
  standalone: true,
  imports: [CommonModule, FormOpenAccountComponent],
  template: `
    <div class="account-container">
      <h2>Open Account</h2>

      @if(!successResponse()){
        <app-form-open-account [isSubmitting]="isSubmitting()" (onValidOpenAccount)="openAccount($event)"></app-form-open-account>
      }

      @if(errorMessage()){
        <section class="summary-error">{{ errorMessage() }}</section>
      }

      @if(successResponse()){
        <section class="success-message">
          <p>Your account has been successfully created.</p>
          <p>Your client ID is : <strong>{{ successResponse()?.username }}</strong></p>
          <p>An activation email has been sent to you. Follow the link to choose your password and activate your account.</p>
          <div class="back-link">
            <a routerLink="/login">Back to Login</a>
          </div>
        </section>
      }
    </div>
  `,
  styleUrls: ['../scss/page-open-account.component.scss']
})
export class OpenAccountPageComponent {
  isSubmitting = signal<boolean>(false);
  successResponse = signal<AccountCreationResponse | null>(null);
  errorMessage = signal<string | null>(null);
  private router = inject(Router);
  private accountApiService = inject(AccountApiService);
  private authService = inject(AuthService);

  openAccount(request: OpenAccountRequest) {
    this.errorMessage.set('');
    this.isSubmitting.set(true);

    this.accountApiService.openAccount(request).subscribe({
      next: (response) => {
        this.successResponse.set(response);
        this.isSubmitting.set(false);
        // clear admin session
        this.authService.clearSession();
      },
      error: (err) => {
        console.error('Failed to create account', err);
        this.errorMessage.set('Unable to create the account. Check the information and try again');
        this.isSubmitting.set(false);
      }
    });
  }
}
