import { AccountGatewayPort } from '../domain/ports/account-gateway.port';
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
} from '../domain/models/account.model';

export class AccountUseCases {
  constructor(private readonly gateway: AccountGatewayPort) {}

  transfer(request: TransferCommand): Promise<AccountResult> {
    return this.gateway.transfer(request);
  }

  openAccount(request: OpenAccountCommand): Promise<AccountCreation> {
    return this.gateway.openAccount(request);
  }

  getAccountSummary(accountNumber: string): Promise<AccountSummary> {
    return this.gateway.getAccountSummary(accountNumber);
  }

  getMyAccount(): Promise<AccountSummary> {
    return this.gateway.getMyAccount();
  }

  addBeneficiary(accountNumber: string, request: BeneficiaryCommand): Promise<Beneficiary> {
    return this.gateway.addBeneficiary(accountNumber, request);
  }

  getBeneficiaries(accountNumber: string): Promise<Beneficiary[]> {
    return this.gateway.getBeneficiaries(accountNumber);
  }

  getActivationEmailPreview(username: string): Promise<ActivationEmailPreview> {
    return this.gateway.getActivationEmailPreview(username);
  }

  getLinkedSavingsAccounts(): Promise<AccountSummary[]> {
    return this.gateway.getLinkedSavingsAccounts();
  }

  getTransactionHistory(accountNumber: string): Promise<Transaction[]> {
    return this.gateway.getTransactionHistory(accountNumber);
  }

  createDirectDebit(accountNumber: string, request: DirectDebitCommand): Promise<DirectDebit> {
    return this.gateway.createDirectDebit(accountNumber, request);
  }

  getDirectDebits(accountNumber: string): Promise<DirectDebit[]> {
    return this.gateway.getDirectDebits(accountNumber);
  }

  cancelDirectDebit(accountNumber: string, directDebitId: string): Promise<void> {
    return this.gateway.cancelDirectDebit(accountNumber, directDebitId);
  }
}
