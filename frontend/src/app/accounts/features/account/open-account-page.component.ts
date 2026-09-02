import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { FormOpenAccountComponent } from '../../ui/forms/form-open-account.component';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { OpenAccountRequest, AccountCreationResponse, ActivationEmailPreview } from '../../domain/entities/account.model';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';
import { environment } from '../../../../environments/environment';
import { ACCOUNT_TYPE_LABELS, AccountType } from '../../domain/entities/account.model';

@Component({
  selector: 'app-page-open-account',
  standalone: true,
  imports: [CommonModule, FormOpenAccountComponent, RouterLink],
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
          <p>Account number : <strong>{{ successResponse()?.accountId }}</strong></p>
          <p>Account type : <strong>{{ accountTypeLabel(successResponse()?.accountType) }}</strong></p>

          @if (successResponse()?.accountType === 'CURRENT') {
            <p>Your client ID is : <strong>{{ successResponse()?.username }}</strong></p>
            <p>Activation link :
              <a [href]="successResponse()?.activationUrl" target="_blank">{{ successResponse()?.activationUrl }}</a>
            </p>
            <!-- ... bloc email preview still same, only for  CURRENT ... -->
          } @else {
            <p>This savings account has no login of its own. It's active immediately and visible from your checking account summary.</p>
          }

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
  private accountApiService = inject(AccountApiService);
  private authService = inject(AuthService);
  private sanitizer = inject(DomSanitizer);
  isProduction = environment.production;

  isSubmitting = signal<boolean>(false);
  successResponse = signal<AccountCreationResponse | null>(null);
  errorMessage = signal<string | null>(null);

  emailPreview = signal<ActivationEmailPreview | null>(null);
  emailLoading = signal(false);
  emailError = signal<string | null>(null);

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

  loadActivationEmail(): void {
    if (!this.successResponse()) return;

    this.emailLoading.set(true);
    this.emailError.set(null);

    this.accountApiService.getActivationEmailPreview(this.successResponse()?.username ||"").subscribe({
      next: (preview) => {
        this.emailPreview.set(preview);
        this.emailLoading.set(false);
      },
      error: () => {
        this.emailLoading.set(false);
        this.emailError.set("Email not yet received (some seconds of delay possible). Please try again.");
      }
    });
  }

  sanitizedHtml() {
    const preview = this.emailPreview();
    if (!preview) return '';

    // HTML email case.
    if (preview.html && preview.html.trim().length > 0) {
      return this.sanitizer.bypassSecurityTrustHtml(preview.html);
    }

    const escaped = this.escapeHtml(preview.text || '');
    const linked = escaped.replace(/(https?:\/\/[^\s]+)/g, '<a href="$1" target="_blank">$1</a>');
    const withBreaks = linked.replace(/\n/g, '<br>');

    return this.sanitizer.bypassSecurityTrustHtml(withBreaks);
  }

  private escapeHtml(text: string): string {
    return text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
  }

  accountTypeLabel(type: AccountType | undefined): string {
    return type ? ACCOUNT_TYPE_LABELS[type] : '';
  }
}
