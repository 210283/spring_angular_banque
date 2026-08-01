import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  TransferRequest,
  BalanceAccount,
  OpenAccountRequest,
  AccountCreationResponse,
  AccountSummaryResponse,
  BeneficiaryRequest,
  BeneficiaryResponse,
  ActivationEmailPreview
} from '../../domain/entities/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountApiService {
  private readonly API_URL = '/api/accounts';

  constructor(private http: HttpClient) {}

  transfer(request: TransferRequest): Observable<BalanceAccount> {
    return this.http.post<BalanceAccount>(`${this.API_URL}/transfer`, request, {responseType: 'text' as 'json'});
  }

  openAccount(request: OpenAccountRequest): Observable<AccountCreationResponse> {
    return this.http.post<AccountCreationResponse>(`${this.API_URL}`, request);
  }

  getAccountSummary(accountNumber: string): Observable<AccountSummaryResponse> {
    return this.http.get<AccountSummaryResponse>(`${this.API_URL}/${accountNumber}/summary`);
  }

  getMyAccount(): Observable<AccountSummaryResponse> {
    return this.http.get<AccountSummaryResponse>(`${this.API_URL}/me`);
  }

  addBeneficiary(accountNumber: string, request: BeneficiaryRequest): Observable<BeneficiaryResponse> {
    return this.http.post<BeneficiaryResponse>(`${this.API_URL}/${accountNumber}/beneficiaries`, request);
  }

  getBeneficiaries(accountNumber: string): Observable<BeneficiaryResponse[]> {
    return this.http.get<BeneficiaryResponse[]>(`${this.API_URL}/${accountNumber}/beneficiaries`);
  }

  getActivationEmailPreview(username: string): Observable<ActivationEmailPreview> {
    return this.http.get<ActivationEmailPreview>(`/api/dev/activation-email/${username}`);
  }
}
