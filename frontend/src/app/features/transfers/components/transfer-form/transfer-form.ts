import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { CreateTransferRequest } from '../../models/transfer.model';
import { accountsMustDifferValidator, transferDateWithinWindowValidator } from './transfer-form.validators';

const ACCOUNT_PATTERN = /^\d{10}$/;

@Component({
  selector: 'app-transfer-form',
  imports: [ReactiveFormsModule],
  templateUrl: './transfer-form.html',
  styleUrl: './transfer-form.scss',
})
export class TransferForm {
  @Input() submitting = false;
  @Output() scheduled = new EventEmitter<CreateTransferRequest>();

  private readonly formBuilder = new FormBuilder();

  readonly form = this.formBuilder.group(
    {
      sourceAccount: ['', [Validators.required, Validators.pattern(ACCOUNT_PATTERN)]],
      destinationAccount: ['', [Validators.required, Validators.pattern(ACCOUNT_PATTERN)]],
      amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
      transferDate: ['', [Validators.required, transferDateWithinWindowValidator()]],
    },
    { validators: accountsMustDifferValidator },
  );

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.scheduled.emit(this.form.getRawValue() as CreateTransferRequest);
  }

  restrictToDigits(event: Event, controlName: 'sourceAccount' | 'destinationAccount'): void {
    const input = event.target as HTMLInputElement;
    const digitsOnly = input.value.replace(/\D/g, '').slice(0, 10);
    if (input.value !== digitsOnly) {
      input.value = digitsOnly;
      this.form.get(controlName)?.setValue(digitsOnly, { emitEvent: false });
    }
  }

  clearAmount(): void {
    const amountControl = this.form.get('amount');
    amountControl?.reset(null);
    amountControl?.markAsPristine();
    amountControl?.markAsUntouched();
  }
}
