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
} from '../entities/account.model';
import { Observable } from 'rxjs';

export interface AccountGatewayPort {
  transfer(request: TransferRequest): Observable<BalanceAccount>;
  openAccount(request: OpenAccountRequest): Observable<AccountCreationResponse>;
  getAccountSummary(accountNumber: string): Observable<AccountSummaryResponse>;
  getMyAccount(): Observable<AccountSummaryResponse>;
  addBeneficiary(accountNumber: string, request: BeneficiaryRequest): Observable<BeneficiaryResponse>;
  getBeneficiaries(accountNumber: string): Observable<BeneficiaryResponse[]>;
  getActivationEmailPreview(username: string): Observable<ActivationEmailPreview>;
  getLinkedSavingsAccounts(): Observable<AccountSummaryResponse[]>;
  getTransactionHistory(accountNumber: string): Observable<TransactionResponse[]>;
  createDirectDebit(accountNumber: string, request: CreateDirectDebitRequest): Observable<DirectDebitResponse>;
  getDirectDebits(accountNumber: string): Observable<DirectDebitResponse[]>;
  cancelDirectDebit(accountNumber: string, directDebitId: string): Observable<void>;
}
