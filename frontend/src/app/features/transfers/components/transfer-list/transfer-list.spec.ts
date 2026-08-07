import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferList } from './transfer-list';

describe('TransferList', () => {
  let component: TransferList;
  let fixture: ComponentFixture<TransferList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferList],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show an empty state message when there are no transfers', () => {
    fixture.detectChanges();

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Nenhuma transferência agendada');
  });

  it('should render a row for each transfer', () => {
    fixture.componentRef.setInput('transfers', [
      {
        id: 1,
        sourceAccount: '1234567890',
        destinationAccount: '0987654321',
        amount: 1000,
        fee: 82,
        totalAmount: 1082,
        transferDate: '2026-08-21',
        schedulingDate: '2026-08-06',
        status: 'SCHEDULED',
      },
    ]);
    fixture.detectChanges();

    const rows = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(rows.length).toBe(1);
  });
});
