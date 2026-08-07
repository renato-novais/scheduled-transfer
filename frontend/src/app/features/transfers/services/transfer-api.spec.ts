import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { TransferApi } from './transfer-api';
import { CreateTransferRequest, TransferResponse } from '../models/transfer.model';

describe('TransferApi', () => {
  let service: TransferApi;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TransferApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should POST a new transfer to /api/v1/transfers', () => {
    const request: CreateTransferRequest = {
      sourceAccount: '1234567890',
      destinationAccount: '0987654321',
      amount: 1000,
      transferDate: '2026-08-21',
    };
    const response: TransferResponse = {
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

    service.schedule(request).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const req = httpMock.expectOne('/api/v1/transfers');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(response);
  });

  it('should GET the list of scheduled transfers', () => {
    const responses: TransferResponse[] = [
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
    ];

    service.list().subscribe((result) => {
      expect(result).toEqual(responses);
    });

    const req = httpMock.expectOne('/api/v1/transfers');
    expect(req.request.method).toBe('GET');
    req.flush(responses);
  });
});
