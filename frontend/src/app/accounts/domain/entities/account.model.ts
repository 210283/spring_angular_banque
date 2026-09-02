export interface TransferRequest {
  sourceAccountNumber: string;
  destinationAccountNumber: string;
  amount: number;
}

export interface BalanceAccount {
  accountId: string;
  newBalance: number;
  status: 'SUCCESS' | 'FAILED';
}

export interface OpenAccountRequest {
  owner: string | null;
  initialDeposit: number;
  accountType: AccountType;
  linkedAccountNumber: string | null;
}

export interface AccountCreationResponse {
  accountId: string;
  username: string;
  activationUrl: string;
  accountType: AccountType;
}

export interface AccountSummaryResponse {
  accountId: string;
  owner: string;
  balance: number;
  accountType: AccountType;
  interestRate: number;
}

export interface BeneficiaryRequest {
  label: string;
  accountNumber: string;
  ownerName: string;
}

export interface BeneficiaryResponse {
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

export type AccountType = 'CURRENT' | 'SAVINGS' | 'BOOKLET' | 'LDD';

export const ACCOUNT_TYPE_LABELS: Record<AccountType, string> = {
  CURRENT: 'Compte Courant',
  SAVINGS: 'Compte Épargne',
  BOOKLET: 'Livret A',
  LDD: 'LDD'
};
