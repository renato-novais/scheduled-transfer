import { FormControl, FormGroup } from '@angular/forms';

import {
  accountsMustDifferValidator,
  transferDateWithinWindowValidator,
} from './transfer-form.validators';

describe('accountsMustDifferValidator', () => {
  it('should return an error when source and destination accounts are equal', () => {
    const group = new FormGroup({
      sourceAccount: new FormControl('1234567890'),
      destinationAccount: new FormControl('1234567890'),
    });

    const result = accountsMustDifferValidator(group);

    expect(result).toEqual({ accountsMustDiffer: true });
  });

  it('should return null when source and destination accounts differ', () => {
    const group = new FormGroup({
      sourceAccount: new FormControl('1234567890'),
      destinationAccount: new FormControl('0987654321'),
    });

    const result = accountsMustDifferValidator(group);

    expect(result).toBeNull();
  });
});

describe('transferDateWithinWindowValidator', () => {
  const today = new Date('2026-08-06T00:00:00');
  const validator = transferDateWithinWindowValidator(today);

  it('should return an error when the date is before today', () => {
    const control = new FormControl('2026-08-05');

    expect(validator(control)).toEqual({ dateOutOfWindow: true });
  });

  it('should return an error when the date is more than 50 days ahead', () => {
    const control = new FormControl('2026-09-26');

    expect(validator(control)).toEqual({ dateOutOfWindow: true });
  });

  it('should return null when the date is today', () => {
    const control = new FormControl('2026-08-06');

    expect(validator(control)).toBeNull();
  });

  it('should return null when the date is exactly 50 days ahead', () => {
    const control = new FormControl('2026-09-25');

    expect(validator(control)).toBeNull();
  });
});
