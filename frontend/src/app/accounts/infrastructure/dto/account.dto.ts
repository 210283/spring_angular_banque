import { AccountType, DirectDebitFrequency, TransactionCategory, TransactionType } from '../../domain/models/account.model';

export interface TransferRequestDto {
  sourceAccountNumber: string;
  destinationAccountNumber: string;
  amount: number;
}

export interface BalanceAccountDto {
  accountId: string;
  newBalance: number;
  status: 'SUCCESS' | 'FAILED';
}

export interface OpenAccountRequestDto {
  owner: string | null;
  initialDeposit: number;
  accountType: AccountType;
  linkedAccountNumber: string | null;
}

export interface AccountCreationResponseDto {
  accountId: string;
  username: string;
  activationUrl: string;
  accountType: AccountType;
}

export interface AccountSummaryResponseDto {
  accountId: string;
  owner: string;
  balance: number;
  accountType: AccountType;
  interestRate: number;
}

export interface BeneficiaryRequestDto {
  label: string;
  accountNumber: string;
  ownerName: string;
}

export interface BeneficiaryResponseDto {
  id: string;
  label: string;
  beneficiaryAccountNumber: string;
  accountName: string;
  accountType: AccountType;
}

export interface ActivationEmailPreviewDto {
  subject: string;
  text: string;
  html: string;
}

export interface TransactionResponseDto {
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

export interface CreateDirectDebitRequestDto {
  beneficiaryAccountNumber: string;
  amount: number;
  frequency: DirectDebitFrequency;
  startDate: string;
}

export interface DirectDebitResponseDto {
  id: string;
  beneficiaryAccountNumber: string;
  beneficiaryLabel: string | null;
  amount: number;
  frequency: DirectDebitFrequency;
  nextExecutionDate: string;
  active: boolean;
}
