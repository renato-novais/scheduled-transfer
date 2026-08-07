import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function accountsMustDifferValidator(group: AbstractControl): ValidationErrors | null {
  const sourceAccount = group.get('sourceAccount')?.value;
  const destinationAccount = group.get('destinationAccount')?.value;

  if (sourceAccount && destinationAccount && sourceAccount === destinationAccount) {
    return { accountsMustDiffer: true };
  }
  return null;
}

export function transferDateWithinWindowValidator(today: Date = new Date()): ValidatorFn {
  const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const maxDate = new Date(startOfToday);
  maxDate.setDate(maxDate.getDate() + 50);

  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }
    const transferDate = parseLocalDate(control.value);
    if (transferDate < startOfToday || transferDate > maxDate) {
      return { dateOutOfWindow: true };
    }
    return null;
  };
}

function parseLocalDate(isoDate: string): Date {
  const [year, month, day] = isoDate.split('-').map(Number);
  return new Date(year, month - 1, day);
}
