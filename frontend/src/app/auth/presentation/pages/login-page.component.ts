import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="login-container">
      <h1>Login</h1>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <label for="username">Identifier</label>
        <input id="username" type="text" formControlName="username" autocomplete="username" />

        <label for="password">Password</label>
        <input id="password" type="password" formControlName="password" autocomplete="current-password" />

        <button type="submit" [disabled]="form.invalid || isSubmitting">
          @if (isSubmitting) {
            <img src="loading.gif" alt="Loading..." />
            Login...
          } @else {
            Login
          }
        </button>

        @if(errorMessage){
          <div class="error">{{ errorMessage }}</div>
        }
      </form>

      <button type="button" (click)="onTryDemo()" [disabled]="isDemoStarting">
        @if (isDemoStarting) {
          <img src="loading.gif" alt="Loading..." />
          Préparation de la démo...
        } @else {
          Tester la démo
        }
      </button>

      @if(demoErrorMessage){
        <div class="error">{{ demoErrorMessage }}</div>
      }

      <p class="signup-link">
        Not yet a customer ?
        <a routerLink="/open-account">Open account</a>
      </p>
    </div>
  `,
  styleUrl: '../../../accounts/features/scss/login-page.component.scss'
})
export class LoginPageComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  isSubmitting = false;
  errorMessage: string | null = null;

  isDemoStarting = false;
  demoErrorMessage: string | null = null;

  onSubmit(): void {
    if (this.form.invalid) return;

    this.isSubmitting = true;
    this.errorMessage = null;
    const { username, password } = this.form.value;

    this.authService.login(username!, password!).subscribe({
      next: () => {
        this.isSubmitting = false;
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        if (returnUrl) {
          this.router.navigateByUrl(returnUrl);
        } else if (this.authService.isAdmin()) {
          this.router.navigate(['/open-account']);
        } else {
          this.router.navigate(['/accounts', 'summary']);
        }
      },
      error: (err: any) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.detail || 'Incorrect username or password.';
      }
    });
  }

  onTryDemo(): void {
    this.isDemoStarting = true;
    this.demoErrorMessage = null;

    this.authService.openDemoSession().subscribe({
      next: () => {
        this.isDemoStarting = false;
        this.router.navigate(['/accounts', 'summary']);
      },
      error: (err: any) => {
        this.isDemoStarting = false;
        if (err.status === 429) {
          this.demoErrorMessage = 'Une démo vient déjà d\'être créée depuis cette adresse, réessayez dans quelques minutes.';
        } else {
          this.demoErrorMessage = 'La démo est momentanément indisponible, réessayez plus tard.';
        }
      }
    });
  }
}
