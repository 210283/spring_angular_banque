import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AccountGatewayPort } from '../../domain/ports/account-gateway.port';
import {
  AccountCreation,
  AccountResult,
  AccountSummary,
  ActivationEmailPreview,
  Beneficiary,
  BeneficiaryCommand,
  DirectDebit,
  DirectDebitCommand,
  OpenAccountCommand,
  Transaction,
  TransferCommand
} from '../../domain/models/account.model';
import {
  AccountCreationResponseDto,
  AccountSummaryResponseDto,
  ActivationEmailPreviewDto,
  BeneficiaryRequestDto,
  BeneficiaryResponseDto,
  BalanceAccountDto,
  CreateDirectDebitRequestDto,
  DirectDebitResponseDto,
  OpenAccountRequestDto,
  TransactionResponseDto,
  TransferRequestDto
} from '../dto/account.dto';

@Injectable({ providedIn: 'root' })
export class AccountHttpAdapter implements AccountGatewayPort {
  private readonly apiUrl = `${environment.apiUrl}/api/accounts`;

  constructor(private readonly http: HttpClient) {}

  transfer(request: TransferCommand): Promise<AccountResult> {
    return firstValueFrom(this.http.post<BalanceAccountDto>(`${this.apiUrl}/transfer`, request as TransferRequestDto, { responseType: 'text' as 'json' }));
  }

  openAccount(request: OpenAccountCommand): Promise<AccountCreation> {
    return firstValueFrom(this.http.post<AccountCreationResponseDto>(this.apiUrl, request as OpenAccountRequestDto));
  }

  getAccountSummary(accountNumber: string): Promise<AccountSummary> {
    return firstValueFrom(this.http.get<AccountSummaryResponseDto>(`${this.apiUrl}/${accountNumber}/summary`));
  }

  getMyAccount(): Promise<AccountSummary> {
    return firstValueFrom(this.http.get<AccountSummaryResponseDto>(`${this.apiUrl}/me`));
  }

  addBeneficiary(accountNumber: string, request: BeneficiaryCommand): Promise<Beneficiary> {
    return firstValueFrom(this.http.post<BeneficiaryResponseDto>(`${this.apiUrl}/${accountNumber}/beneficiaries`, request as BeneficiaryRequestDto));
  }

  getBeneficiaries(accountNumber: string): Promise<Beneficiary[]> {
    return firstValueFrom(this.http.get<BeneficiaryResponseDto[]>(`${this.apiUrl}/${accountNumber}/beneficiaries`));
  }

  getActivationEmailPreview(username: string): Promise<ActivationEmailPreview> {
    return firstValueFrom(this.http.get<ActivationEmailPreviewDto>(`${environment.apiUrl}/api/dev/activation-email/${username}`));
  }

  getLinkedSavingsAccounts(): Promise<AccountSummary[]> {
    return firstValueFrom(this.http.get<AccountSummaryResponseDto[]>(`${this.apiUrl}/me/savings-accounts`));
  }

  getTransactionHistory(accountNumber: string): Promise<Transaction[]> {
    return firstValueFrom(this.http.get<TransactionResponseDto[]>(`${this.apiUrl}/${accountNumber}/transactions`));
  }

  createDirectDebit(accountNumber: string, request: DirectDebitCommand): Promise<DirectDebit> {
    return firstValueFrom(this.http.post<DirectDebitResponseDto>(`${this.apiUrl}/${accountNumber}/direct-debits`, request as CreateDirectDebitRequestDto));
  }

  getDirectDebits(accountNumber: string): Promise<DirectDebit[]> {
    return firstValueFrom(this.http.get<DirectDebitResponseDto[]>(`${this.apiUrl}/${accountNumber}/direct-debits`));
  }

  cancelDirectDebit(accountNumber: string, directDebitId: string): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.apiUrl}/${accountNumber}/direct-debits/${directDebitId}`));
  }
}
