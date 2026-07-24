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
  owner: string;
  initialDeposit: number;
}

export interface AccountCreationResponse {
  accountId: string;
  username: string;
}

export interface AccountSummaryResponse {
  accountId: string;
  owner: string;
  balance: number;
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
}
