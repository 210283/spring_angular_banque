import { Component, inject, signal, OnInit } from '@angular/core';
import { AccountUseCases } from '../../application/account.use-cases';
import { ACCOUNT_USE_CASES } from '../../../app.config';
import { FormAddBeneficiaryComponent } from '../../ui/forms/form-add-beneficiary.component';
import { Router } from '@angular/router';
import { BeneficiaryCommand } from '../../domain/models/account.model';
import { from } from 'rxjs';

@Component({
  selector: 'app-add-beneficiary-page',
  standalone: true,
  imports: [FormAddBeneficiaryComponent],
  template: `
    <div class="container">
      <h2>Add a new beneficiary</h2>

      @if (loading()) {
        <section class="summary-loading"><img src="loading.gif" alt="Loading..." /></section>
      }

      @if (errorMessage()) {
        <p class="error">{{ errorMessage() }}</p>
      }

      @if (!loading()) {
        <app-form-add-beneficiary [isSubmitting]="isSubmitting()" (onSubmitBeneficiary)="addBeneficiary($event)" />
      }
    </div>
  `,
  styleUrl: '../scss/add-beneficiary-page.component.scss'
})
export class AddBeneficiaryPageComponent implements OnInit {
  private apiService = inject(ACCOUNT_USE_CASES);
  private router = inject(Router);

  errorMessage = signal<string | null>(null);
  loading = signal(false);
  isSubmitting = signal(false);

  private currentAccountNumber: string | null = null;

  ngOnInit(): void {
    this.loading.set(true);

    from(this.apiService.getMyAccount()).subscribe({
      next: (summary) => {
        this.currentAccountNumber = summary.accountId;
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to retrieve your account.');
        this.loading.set(false);
      }
    });
  }

  addBeneficiary(request: BeneficiaryCommand) {
    if (!this.currentAccountNumber) {
      this.errorMessage.set("Unable to retrieve the current account number.");
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    from(this.apiService.addBeneficiary(this.currentAccountNumber, request)).subscribe({
      next: () => {
        alert("Beneficiary added successfully.");
        this.router.navigate(['/accounts', 'transfer']);
      },
      error: () => {
        this.isSubmitting.set(false);
        this.errorMessage.set("An error occurred while adding the beneficiary.");
      }
    });
  }
}
