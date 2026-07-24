import { Component, inject, signal, OnInit } from '@angular/core';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { FormAddBeneficiaryComponent } from '../../ui/forms/form-add-beneficiary.component';
import { Router } from '@angular/router';
import { BeneficiaryRequest } from '../../domain/entities/account.model';

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

      @if (!loading() && !errorMessage()) {
        <app-form-add-beneficiary (onSubmitBeneficiary)="addBeneficiary($event)" />
      }
    </div>
  `,
  styleUrl: '../scss/add-beneficiary-page.component.scss'
})
export class AddBeneficiaryPageComponent implements OnInit {
  private apiService = inject(AccountApiService);
  private router = inject(Router);

  errorMessage = signal<string | null>(null);
  loading = signal(false);

  private currentAccountNumber: string | null = null;

  ngOnInit(): void {
    this.loading.set(true);

    this.apiService.getMyAccount().subscribe({
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

  addBeneficiary(request: BeneficiaryRequest) {
    if (!this.currentAccountNumber) {
      this.errorMessage.set("Unable to retrieve the current account number.");
      return;
    }

    this.apiService.addBeneficiary(this.currentAccountNumber, request).subscribe({
      next: () => {
        alert("Beneficiary added successfully.");
        this.router.navigate(['/accounts', 'transfer']);
      },
      error: () => {
        this.errorMessage.set("An error occurred while adding the beneficiary.");
      }
    });
  }
}
