export interface LoginResponse {
  token: string;
}

export interface AuthGatewayPort {
  login(username: string, password: string): Promise<LoginResponse>;
  activate(username: string, token: string, newPassword: string): Promise<void>;
}
