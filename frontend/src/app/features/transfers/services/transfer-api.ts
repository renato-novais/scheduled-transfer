import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { CreateTransferRequest, TransferResponse } from '../models/transfer.model';

@Service()
export class TransferApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/transfers';

  schedule(request: CreateTransferRequest): Observable<TransferResponse> {
    return this.http.post<TransferResponse>(this.baseUrl, request);
  }

  list(): Observable<TransferResponse[]> {
    return this.http.get<TransferResponse[]>(this.baseUrl);
  }
}
