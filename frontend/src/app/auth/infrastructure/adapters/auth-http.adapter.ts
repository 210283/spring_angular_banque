import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthGatewayPort, LoginResponse } from '../../domain/ports/auth-gateway.port';

@Injectable({ providedIn: 'root' })
export class AuthHttpAdapter implements AuthGatewayPort {
  private readonly apiUrl = `${environment.apiUrl}/api/auth`;

  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { username, password });
  }

  activate(username: string, token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/activate`, { username, token, newPassword });
  }
}
