import { Injectable, computed, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, from, map, tap } from 'rxjs';
import { AUTH_GATEWAY } from './infrastructure/auth-gateway.token';
import { LoginResponse } from './domain/ports/auth-gateway.port';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'votrebanque.token';
  private readonly DEMO_CLIENT_TOKEN_KEY = 'votrebanque.demoClientToken';
  private readonly DEMO_ADMIN_TOKEN_KEY = 'votrebanque.demoAdminToken';
  private authGateway = inject(AUTH_GATEWAY);

  private token = signal<string | null>(null);
  private username = signal<string | null>(null);
  private role = signal<string | null>(null);
  private demoModeActive = signal<boolean>(false);

  readonly isAuthenticated = computed(() => this.token() !== null);
  readonly currentUsername = computed(() => this.username());
  readonly isAdmin = computed(() => this.role() === 'ROLE_ADMIN');
  readonly canOpenAccounts = computed(() => this.role() === 'ROLE_ADMIN' || this.role() === 'ROLE_DEMO_ADMIN');
  readonly isDemoSession = computed(() => this.demoModeActive());

  constructor(private router: Router) {
    const savedToken = localStorage.getItem(this.TOKEN_KEY);
    if (savedToken && !this.isTokenExpired(savedToken)) {
      this.token.set(savedToken);
      this.username.set(this.extractClaim(savedToken, 'sub'));
      this.role.set(this.extractClaim(savedToken, 'role'));
    } else if (savedToken) {
      localStorage.removeItem(this.TOKEN_KEY);
    }
    this.demoModeActive.set(!!localStorage.getItem(this.DEMO_ADMIN_TOKEN_KEY));
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return from(this.authGateway.login(username, password)).pipe(
      tap(response => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        this.token.set(response.token);
        this.username.set(this.extractClaim(response.token, 'sub'));
        this.role.set(this.extractClaim(response.token, 'role'));
      })
    );
  }

  openDemoSession(): Observable<void> {
    return from(this.authGateway.openDemoSession()).pipe(
      tap(response => {
        localStorage.setItem(this.DEMO_CLIENT_TOKEN_KEY, response.clientToken);
        localStorage.setItem(this.DEMO_ADMIN_TOKEN_KEY, response.adminToken);
        this.demoModeActive.set(true);
        this.activateToken(response.clientToken);
      }),
      map(() => undefined)
    );
  }

  // Bascule instantanée entre le compte client démo et l'admin démo, sans nouvel appel réseau.
  switchToDemoClient(): void {
    const token = localStorage.getItem(this.DEMO_CLIENT_TOKEN_KEY);
    if (token) {
      this.activateToken(token);
    }
  }

  switchToDemoAdmin(): void {
    const token = localStorage.getItem(this.DEMO_ADMIN_TOKEN_KEY);
    if (token) {
      this.activateToken(token);
    }
  }

  private activateToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.token.set(token);
    this.username.set(this.extractClaim(token, 'sub'));
    this.role.set(this.extractClaim(token, 'role'));
  }

  logout(): void {
    this.clearSession();
    localStorage.removeItem(this.DEMO_CLIENT_TOKEN_KEY);
    localStorage.removeItem(this.DEMO_ADMIN_TOKEN_KEY);
    this.demoModeActive.set(false);
    this.router.navigate(['/login']);
  }

  // Vide la session sans redirection — utilisé après une action admin ponctuelle
  clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.token.set(null);
    this.username.set(null);
    this.role.set(null);
  }

  getToken(): string | null {
    return this.token();
  }

  private extractClaim(token: string, claim: string): string | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload[claim] ?? null;
    } catch {
      return null;
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiryMs = payload.exp * 1000;
      return Date.now() >= expiryMs;
    } catch {
      return true;
    }
  }
}
