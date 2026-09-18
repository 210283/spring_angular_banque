import { InjectionToken } from '@angular/core';
import { AuthGatewayPort } from '../domain/ports/auth-gateway.port';

export const AUTH_GATEWAY = new InjectionToken<AuthGatewayPort>('AUTH_GATEWAY');
