import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AccountGatewayPort } from '../../domain/ports/account-gateway.port';
import {
  AccountCreationResponse,
  AccountSummaryResponse,
  ActivationEmailPreview,
  BeneficiaryRequest,
  BeneficiaryResponse,
  BalanceAccount,
  CreateDirectDebitRequest,
  DirectDebitResponse,
  OpenAccountRequest,
  TransactionResponse,
  TransferRequest
} from '../../domain/entities/account.model';

@Injectable({ providedIn: 'root' })
export class AccountHttpAdapter implements AccountGatewayPort {
  private readonly apiUrl = `${environment.apiUrl}/api/accounts`;

  constructor(private readonly http: HttpClient) {}

  transfer(request: TransferRequest): Observable<BalanceAccount> {
    return this.http.post<BalanceAccount>(`${this.apiUrl}/transfer`, request, { responseType: 'text' as 'json' });
  }

  openAccount(request: OpenAccountRequest): Observable<AccountCreationResponse> {
    return this.http.post<AccountCreationResponse>(this.apiUrl, request);
  }

  getAccountSummary(accountNumber: string): Observable<AccountSummaryResponse> {
    return this.http.get<AccountSummaryResponse>(`${this.apiUrl}/${accountNumber}/summary`);
  }

  getMyAccount(): Observable<AccountSummaryResponse> {
    return this.http.get<AccountSummaryResponse>(`${this.apiUrl}/me`);
  }

  addBeneficiary(accountNumber: string, request: BeneficiaryRequest): Observable<BeneficiaryResponse> {
    return this.http.post<BeneficiaryResponse>(`${this.apiUrl}/${accountNumber}/beneficiaries`, request);
  }

  getBeneficiaries(accountNumber: string): Observable<BeneficiaryResponse[]> {
    return this.http.get<BeneficiaryResponse[]>(`${this.apiUrl}/${accountNumber}/beneficiaries`);
  }

  getActivationEmailPreview(username: string): Observable<ActivationEmailPreview> {
    return this.http.get<ActivationEmailPreview>(`${environment.apiUrl}/api/dev/activation-email/${username}`);
  }

  getLinkedSavingsAccounts(): Observable<AccountSummaryResponse[]> {
    return this.http.get<AccountSummaryResponse[]>(`${this.apiUrl}/me/savings-accounts`);
  }

  getTransactionHistory(accountNumber: string): Observable<TransactionResponse[]> {
    return this.http.get<TransactionResponse[]>(`${this.apiUrl}/${accountNumber}/transactions`);
  }

  createDirectDebit(accountNumber: string, request: CreateDirectDebitRequest): Observable<DirectDebitResponse> {
    return this.http.post<DirectDebitResponse>(`${this.apiUrl}/${accountNumber}/direct-debits`, request);
  }

  getDirectDebits(accountNumber: string): Observable<DirectDebitResponse[]> {
    return this.http.get<DirectDebitResponse[]>(`${this.apiUrl}/${accountNumber}/direct-debits`);
  }

  cancelDirectDebit(accountNumber: string, directDebitId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${accountNumber}/direct-debits/${directDebitId}`);
  }
}
