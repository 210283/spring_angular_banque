import { Observable } from 'rxjs';
import { AccountGatewayPort } from '../domain/ports/account-gateway.port';
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
} from '../domain/entities/account.model';

export class AccountUseCases {
  constructor(private readonly gateway: AccountGatewayPort) {}

  transfer(request: TransferRequest): Observable<BalanceAccount> {
    return this.gateway.transfer(request);
  }

  openAccount(request: OpenAccountRequest): Observable<AccountCreationResponse> {
    return this.gateway.openAccount(request);
  }

  getAccountSummary(accountNumber: string): Observable<AccountSummaryResponse> {
    return this.gateway.getAccountSummary(accountNumber);
  }

  getMyAccount(): Observable<AccountSummaryResponse> {
    return this.gateway.getMyAccount();
  }

  addBeneficiary(accountNumber: string, request: BeneficiaryRequest): Observable<BeneficiaryResponse> {
    return this.gateway.addBeneficiary(accountNumber, request);
  }

  getBeneficiaries(accountNumber: string): Observable<BeneficiaryResponse[]> {
    return this.gateway.getBeneficiaries(accountNumber);
  }

  getActivationEmailPreview(username: string): Observable<ActivationEmailPreview> {
    return this.gateway.getActivationEmailPreview(username);
  }

  getLinkedSavingsAccounts(): Observable<AccountSummaryResponse[]> {
    return this.gateway.getLinkedSavingsAccounts();
  }

  getTransactionHistory(accountNumber: string): Observable<TransactionResponse[]> {
    return this.gateway.getTransactionHistory(accountNumber);
  }

  createDirectDebit(accountNumber: string, request: CreateDirectDebitRequest): Observable<DirectDebitResponse> {
    return this.gateway.createDirectDebit(accountNumber, request);
  }

  getDirectDebits(accountNumber: string): Observable<DirectDebitResponse[]> {
    return this.gateway.getDirectDebits(accountNumber);
  }

  cancelDirectDebit(accountNumber: string, directDebitId: string): Observable<void> {
    return this.gateway.cancelDirectDebit(accountNumber, directDebitId);
  }
}
