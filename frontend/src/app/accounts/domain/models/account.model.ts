export type AccountType = 'CURRENT' | 'SAVINGS' | 'BOOKLET' | 'LDD';
export type TransactionType = 'DEBIT' | 'CREDIT';
export type TransactionCategory = 'OPENING_DEPOSIT' | 'TRANSFER' | 'DIRECT_DEBIT' | 'INTEREST';
export type DirectDebitFrequency = 'WEEKLY' | 'MONTHLY';

export interface TransferCommand {
  sourceAccountNumber: string;
  destinationAccountNumber: string;
  amount: number;
}

export interface AccountResult {
  accountId: string;
  newBalance: number;
  status: 'SUCCESS' | 'FAILED';
}

export interface OpenAccountCommand {
  owner: string | null;
  initialDeposit: number;
  accountType: AccountType;
  linkedAccountNumber: string | null;
}

export interface AccountCreation {
  accountId: string;
  username: string;
  activationUrl: string;
  accountType: AccountType;
}

export interface AccountSummary {
  accountId: string;
  owner: string;
  balance: number;
  accountType: AccountType;
  interestRate: number;
}

export interface BeneficiaryCommand {
  label: string;
  accountNumber: string;
  ownerName: string;
}

export interface Beneficiary {
  id: string;
  label: string;
  beneficiaryAccountNumber: string;
  accountName: string;
  accountType: AccountType;
}

export interface ActivationEmailPreview {
  subject: string;
  text: string;
  html: string;
}

export interface Transaction {
  id: string;
  type: TransactionType;
  category: TransactionCategory;
  amount: number;
  balanceAfter: number;
  counterpartyAccountNumber: string | null;
  counterpartyName: string | null;
  label: string;
  occurredAt: string;
}

export interface DirectDebitCommand {
  beneficiaryAccountNumber: string;
  amount: number;
  frequency: DirectDebitFrequency;
  startDate: string;
}

export interface DirectDebit {
  id: string;
  beneficiaryAccountNumber: string;
  beneficiaryLabel: string | null;
  amount: number;
  frequency: DirectDebitFrequency;
  nextExecutionDate: string;
  active: boolean;
}

export const ACCOUNT_TYPE_LABELS: Record<AccountType, string> = {
  CURRENT: 'Compte Courant',
  SAVINGS: 'Compte Épargne',
  BOOKLET: 'Livret A',
  LDD: 'LDD'
};
