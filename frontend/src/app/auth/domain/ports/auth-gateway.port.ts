export interface LoginResponse {
  token: string;
}

export interface DemoSessionResponse {
  token: string;
  mainAccountNumber: string;
  secondaryAccountNumber: string;
  expiresAt: string;
}

export interface AuthGatewayPort {
  login(username: string, password: string): Promise<LoginResponse>;
  activate(username: string, token: string, newPassword: string): Promise<void>;
  openDemoSession(): Promise<DemoSessionResponse>;
}
