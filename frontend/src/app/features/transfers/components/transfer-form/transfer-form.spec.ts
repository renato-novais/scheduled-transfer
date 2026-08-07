import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferForm } from './transfer-form';

describe('TransferForm', () => {
  let component: TransferForm;
  let fixture: ComponentFixture<TransferForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferForm],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not emit when the form is invalid', () => {
    const emitSpy = vi.spyOn(component.scheduled, 'emit');

    component.onSubmit();

    expect(emitSpy).not.toHaveBeenCalled();
  });

  it('should mark all fields as touched when submitting an invalid form', () => {
    component.onSubmit();

    expect(component.form.get('sourceAccount')?.touched).toBe(true);
  });

  it('should emit the request payload when the form is valid', () => {
    const emitSpy = vi.spyOn(component.scheduled, 'emit');
    component.form.setValue({
      sourceAccount: '1234567890',
      destinationAccount: '0987654321',
      amount: 1000,
      transferDate: '2026-08-21',
    });

    component.onSubmit();

    expect(emitSpy).toHaveBeenCalledWith({
      sourceAccount: '1234567890',
      destinationAccount: '0987654321',
      amount: 1000,
      transferDate: '2026-08-21',
    });
  });

  it('should not emit when source and destination accounts are the same', () => {
    const emitSpy = vi.spyOn(component.scheduled, 'emit');
    component.form.setValue({
      sourceAccount: '1234567890',
      destinationAccount: '1234567890',
      amount: 1000,
      transferDate: '2026-08-21',
    });

    component.onSubmit();

    expect(emitSpy).not.toHaveBeenCalled();
  });
});
