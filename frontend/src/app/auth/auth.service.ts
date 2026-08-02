import { Injectable, computed, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'votrebanque.token';
  private http = inject(HttpClient);

  private token = signal<string | null>(null);
  private username = signal<string | null>(null);
  private role = signal<string | null>(null);

  readonly isAuthenticated = computed(() => this.token() !== null);
  readonly currentUsername = computed(() => this.username());
  readonly isAdmin = computed(() => this.role() === 'ROLE_ADMIN');

  constructor(private router: Router) {
    const savedToken = localStorage.getItem(this.TOKEN_KEY);
    if (savedToken && !this.isTokenExpired(savedToken)) {
      this.token.set(savedToken);
      this.username.set(this.extractClaim(savedToken, 'sub'));
      this.role.set(this.extractClaim(savedToken, 'role'));
    } else if (savedToken) {
      localStorage.removeItem(this.TOKEN_KEY);
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/api/auth/login`, { username, password }).pipe(
      tap(response => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        this.token.set(response.token);
        this.username.set(this.extractClaim(response.token, 'sub'));
        this.role.set(this.extractClaim(response.token, 'role'));
      })
    );
  }

  logout(): void {
    this.clearSession();
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
