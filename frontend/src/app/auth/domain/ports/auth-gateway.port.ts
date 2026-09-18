import { Observable } from 'rxjs';

export interface LoginResponse {
  token: string;
}

export interface AuthGatewayPort {
  login(username: string, password: string): Observable<LoginResponse>;
  activate(username: string, token: string, newPassword: string): Observable<void>;
}
