import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { TransferSchedulePage } from './transfer-schedule-page';
import { TransferApi } from '../../services/transfer-api';
import { CreateTransferRequest, TransferResponse } from '../../models/transfer.model';

describe('TransferSchedulePage', () => {
  let component: TransferSchedulePage;
  let fixture: ComponentFixture<TransferSchedulePage>;
  let transferApi: { list: ReturnType<typeof vi.fn>; schedule: ReturnType<typeof vi.fn> };

  const sampleResponse: TransferResponse = {
    id: 1,
    sourceAccount: '1234567890',
    destinationAccount: '0987654321',
    amount: 1000,
    fee: 82,
    totalAmount: 1082,
    transferDate: '2026-08-21',
    schedulingDate: '2026-08-06',
    status: 'SCHEDULED',
  };

  beforeEach(async () => {
    transferApi = {
      list: vi.fn().mockReturnValue(of([sampleResponse])),
      schedule: vi.fn().mockReturnValue(of(sampleResponse)),
    };

    await TestBed.configureTestingModule({
      imports: [TransferSchedulePage],
      providers: [{ provide: TransferApi, useValue: transferApi }],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferSchedulePage);
    component = fixture.componentInstance;
  });

  it('should load transfers on init', () => {
    fixture.detectChanges();

    expect(transferApi.list).toHaveBeenCalled();
    expect(component.transfers()).toEqual([sampleResponse]);
  });

  it('should schedule a transfer, show a success message and reload the list', () => {
    fixture.detectChanges();
    const request: CreateTransferRequest = {
      sourceAccount: '1234567890',
      destinationAccount: '0987654321',
      amount: 1000,
      transferDate: '2026-08-21',
    };

    component.onScheduled(request);

    expect(transferApi.schedule).toHaveBeenCalledWith(request);
    expect(component.successMessage()).toContain('82');
    expect(component.errorMessage()).toBeNull();
    expect(transferApi.list).toHaveBeenCalledTimes(2);
  });

  it('should show the error message returned by the API when scheduling fails', () => {
    fixture.detectChanges();
    transferApi.schedule.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 400,
            error: { code: 'FEE_NOT_APPLICABLE', message: 'No fee is applicable.' },
          }),
      ),
    );

    component.onScheduled({
      sourceAccount: '1234567890',
      destinationAccount: '0987654321',
      amount: 1000,
      transferDate: '2026-09-30',
    });

    expect(component.errorMessage()).toBe('No fee is applicable.');
    expect(component.successMessage()).toBeNull();
  });
});
