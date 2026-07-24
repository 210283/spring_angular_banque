import { Component, output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BeneficiaryRequest } from '../../domain/entities/account.model';

@Component({
  selector: 'app-form-add-beneficiary',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <form [formGroup]="beneficiaryForm" (ngSubmit)="onSubmit()">
      <div>
        <label for="label">Custom name (Label):</label>
        <input id="label" formControlName="label" type="text" />
      </div>
      <div>
        <label for="accountNumber">Account number :</label>
        <input id="accountNumber" formControlName="accountNumber" type="text" />
      </div>
      <div>
        <label for="ownerName">Owner name :</label>
        <input id="ownerName" formControlName="ownerName" type="text" />
      </div>
      <button type="submit" [disabled]="beneficiaryForm.invalid">Add beneficiary</button>
    </form>
  `,
  styleUrl: '../../features/scss/form-add-beneficiary.component.scss'
})
export class FormAddBeneficiaryComponent {
  private fb = inject(FormBuilder);

  onSubmitBeneficiary = output<BeneficiaryRequest>();

  beneficiaryForm = this.fb.nonNullable.group({
    label: ['', [Validators.required]],
    accountNumber: ['', [Validators.required]],
    ownerName: ['', [Validators.required]]
  });

  onSubmit() {
    if (this.beneficiaryForm.valid) {
      this.onSubmitBeneficiary.emit(this.beneficiaryForm.getRawValue());
    }
  }
}
