import { Routes } from '@angular/router';
import { TransferPageComponent } from './accounts/features/account/transfer-page.component';
import { OpenAccountPageComponent } from './accounts/features/account/open-account-page.component';
import { SummaryPageComponent } from './accounts/features/account/summary-page.component';
import { AuthGuard } from './auth/auth.guard';
import { AdminGuard } from './auth/admin.guard';
import { AddBeneficiaryPageComponent } from './accounts/features/account/add-beneficiary-page.component';
import { LoginPageComponent } from './accounts/features/account/login-page.component';
import { ActivatePageComponent } from './accounts/features/account/activate-page.component';
import { DirectDebitsPageComponent } from './accounts/features/account/direct-debits-page.component';
import { TransactionHistoryPageComponent } from './accounts/features/account/transaction-history-page.component';

export const routes: Routes = [
  { path: 'login', component: LoginPageComponent },
  { path: 'activate', component: ActivatePageComponent },
  { path: 'accounts/transfer', component: TransferPageComponent, canActivate: [AuthGuard] },
  { path: 'open-account', component: OpenAccountPageComponent, canActivate: [AdminGuard] },
  { path: 'accounts/summary', component: SummaryPageComponent, canActivate: [AuthGuard] },
  { path: 'accounts/add-beneficiary', component: AddBeneficiaryPageComponent, canActivate: [AuthGuard] },
  { path: 'accounts/transactions', component: TransactionHistoryPageComponent, canActivate: [AuthGuard] },
  { path: 'accounts/direct-debits', component: DirectDebitsPageComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
