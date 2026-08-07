import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';

import { TransferForm } from '../../components/transfer-form/transfer-form';
import { TransferList } from '../../components/transfer-list/transfer-list';
import { ApiErrorResponse, CreateTransferRequest, TransferResponse } from '../../models/transfer.model';
import { TransferApi } from '../../services/transfer-api';

@Component({
  selector: 'app-transfer-schedule-page',
  imports: [TransferForm, TransferList],
  templateUrl: './transfer-schedule-page.html',
  styleUrl: './transfer-schedule-page.scss',
})
export class TransferSchedulePage implements OnInit {
  private readonly transferApi = inject(TransferApi);

  readonly transfers = signal<TransferResponse[]>([]);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly successMessage = signal<string | null>(null);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadTransfers();
  }

  onScheduled(request: CreateTransferRequest): void {
    this.submitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    this.transferApi.schedule(request).subscribe({
      next: (response) => {
        this.submitting.set(false);
        this.successMessage.set(
          `Transferência agendada com sucesso. Taxa aplicada: R$ ${response.fee.toFixed(2)}.`,
        );
        this.loadTransfers();
      },
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);
        this.errorMessage.set(this.extractErrorMessage(error));
      },
    });
  }

  private loadTransfers(): void {
    this.loading.set(true);
    this.transferApi.list().subscribe({
      next: (transfers) => {
        this.transfers.set(transfers);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Não foi possível carregar os agendamentos.');
        this.loading.set(false);
      },
    });
  }

  private extractErrorMessage(error: HttpErrorResponse): string {
    const apiError = error.error as ApiErrorResponse | undefined;
    return apiError?.message ?? 'Não foi possível agendar a transferência.';
  }
}
