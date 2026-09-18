import { ApplicationConfig, InjectionToken, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { jwtInterceptor } from './auth/jwt.interceptor';
import { AccountGatewayPort } from './accounts/domain/ports/account-gateway.port';
import { AccountUseCases } from './accounts/application/account.use-cases';
import { AccountHttpAdapter } from './accounts/infrastructure/adapters/account-http.adapter';
import { AUTH_GATEWAY } from './auth/infrastructure/auth-gateway.token';
import { AuthHttpAdapter } from './auth/infrastructure/adapters/auth-http.adapter';

export const ACCOUNT_GATEWAY = new InjectionToken<AccountGatewayPort>('ACCOUNT_GATEWAY');
export const ACCOUNT_USE_CASES = new InjectionToken<AccountUseCases>('ACCOUNT_USE_CASES');

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    { provide: AUTH_GATEWAY, useExisting: AuthHttpAdapter },
    { provide: ACCOUNT_GATEWAY, useExisting: AccountHttpAdapter },
    {
      provide: ACCOUNT_USE_CASES,
      useFactory: (gateway: AccountGatewayPort) => new AccountUseCases(gateway),
      deps: [ACCOUNT_GATEWAY]
    }
  ],
};
