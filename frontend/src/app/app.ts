import { Component, inject, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('Votre Banque');
  protected readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  switchToDemoAdmin(): void {
    this.authService.switchToDemoAdmin();
    this.router.navigate(['/open-account']);
  }

  switchToDemoClient(): void {
    this.authService.switchToDemoClient();
    this.router.navigate(['/accounts', 'summary']);
  }
}
