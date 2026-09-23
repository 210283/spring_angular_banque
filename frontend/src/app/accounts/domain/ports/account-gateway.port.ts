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
} from '../models/account.model';

export interface AccountGatewayPort {
  transfer(request: TransferCommand): Promise<AccountResult>;
  openAccount(request: OpenAccountCommand): Promise<AccountCreation>;
  getAccountSummary(accountNumber: string): Promise<AccountSummary>;
  getMyAccount(): Promise<AccountSummary>;
  addBeneficiary(accountNumber: string, request: BeneficiaryCommand): Promise<Beneficiary>;
  getBeneficiaries(accountNumber: string): Promise<Beneficiary[]>;
  getActivationEmailPreview(username: string): Promise<ActivationEmailPreview>;
  getLinkedSavingsAccounts(): Promise<AccountSummary[]>;
  getTransactionHistory(accountNumber: string): Promise<Transaction[]>;
  createDirectDebit(accountNumber: string, request: DirectDebitCommand): Promise<DirectDebit>;
  getDirectDebits(accountNumber: string): Promise<DirectDebit[]>;
  cancelDirectDebit(accountNumber: string, directDebitId: string): Promise<void>;
}
