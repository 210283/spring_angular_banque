import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-activate-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="activation-container">
      <h1>Activating your account</h1>

      @if(linkError){
        <div class="error">{{ linkError }}</div>
      }

      @if(username && token){
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <p>Identifier : <strong>{{ username }}</strong></p>

          <label for="newPassword">Choose your password</label>
          <input id="newPassword" type="password" formControlName="newPassword" />

          @if(form.get('newPassword')?.touched && form.get('newPassword')?.invalid){
            <div class="field-error">
              The password must contain at least 10 characters, an uppercase letter, and a digit.
            </div>
          }

          <label for="confirmPassword">Confirm your password</label>
          <input id="confirmPassword" type="password" formControlName="confirmPassword" />

          @if(form.errors?.['passwordsMismatch'] && form.get('confirmPassword')?.touched){
            <div class="field-error">
              The passwords do not match.
            </div>
          }

          <button type="submit" [disabled]="form.invalid || isSubmitting">
            {{ isSubmitting ? 'Activation in progress...' : 'Activate my account' }}
          </button>

          @if(submitError){
            <div class="error">{{ submitError }}</div>
          }

          @if(successMessage){
            <div class="success">{{ successMessage }}</div>
          }
        </form>
      }
    </div>
  `,
  styleUrl: '../scss/activation-page.component.scss'
})
export class ActivatePageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);

  username: string | null = null;
  token: string | null = null;
  linkError: string | null = null;
  submitError: string | null = null;
  successMessage: string | null = null;
  isSubmitting = false;

  form = this.fb.group({
    newPassword: ['', [
      Validators.required,
      Validators.minLength(10),
      Validators.pattern(/(?=.*[A-Z])(?=.*[0-9])/)
    ]],
    confirmPassword: ['', Validators.required]
  }, { validators: this.passwordsMatchValidator });

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.username = params.get('user');
      this.token = params.get('token');

      if (!this.username || !this.token) {
        this.linkError = "Invalid or incomplete activation link.";
      }
    });
  }

  private passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('newPassword')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordsMismatch: true };
  }

  onSubmit(): void {
    if (this.form.invalid || !this.username || !this.token) {
      return;
    }

    this.isSubmitting = true;
    this.submitError = null;

    const payload = {
      username: this.username,
      token: this.token,
      newPassword: this.form.value.newPassword
    };

    this.http.post('/api/auth/activate', payload).subscribe({
      next: () => {
        this.successMessage = 'Your account has been activated successfully!';
        this.isSubmitting = false;
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.submitError = err.error?.detail || "An error occurred during activation.";
      }
    });
  }
}
