import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild, inject, signal } from '@angular/core';

import { TransferForm } from '../../components/transfer-form/transfer-form';
import { TransferList } from '../../components/transfer-list/transfer-list';
import { ApiErrorResponse, CreateTransferRequest, TransferResponse } from '../../models/transfer.model';
import { TransferApi } from '../../services/transfer-api';
import { ToastNotification } from '../../../../shared/components/toast-notification/toast-notification';

const TOAST_DURATION_MS = 5000;

@Component({
  selector: 'app-transfer-schedule-page',
  imports: [TransferForm, TransferList, ToastNotification],
  templateUrl: './transfer-schedule-page.html',
  styleUrl: './transfer-schedule-page.scss',
})
export class TransferSchedulePage implements OnInit, OnDestroy {
  private readonly transferApi = inject(TransferApi);

  @ViewChild(TransferForm) private transferForm?: TransferForm;

  private successTimeoutId?: ReturnType<typeof setTimeout>;
  private errorTimeoutId?: ReturnType<typeof setTimeout>;

  readonly transfers = signal<TransferResponse[]>([]);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly successMessage = signal<string | null>(null);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadTransfers();
  }

  ngOnDestroy(): void {
    clearTimeout(this.successTimeoutId);
    clearTimeout(this.errorTimeoutId);
  }

  onScheduled(request: CreateTransferRequest): void {
    this.submitting.set(true);
    this.dismissSuccess();
    this.dismissError();

    this.transferApi.schedule(request).subscribe({
      next: (response) => {
        this.submitting.set(false);
        this.showSuccess(`Transferência agendada com sucesso. Taxa aplicada: R$ ${response.fee.toFixed(2)}.`);
        this.transferForm?.clearAmount();
        this.loadTransfers();
      },
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);
        this.showError(this.extractErrorMessage(error));
      },
    });
  }

  dismissSuccess(): void {
    clearTimeout(this.successTimeoutId);
    this.successMessage.set(null);
  }

  dismissError(): void {
    clearTimeout(this.errorTimeoutId);
    this.errorMessage.set(null);
  }

  private showSuccess(message: string): void {
    this.successMessage.set(message);
    clearTimeout(this.successTimeoutId);
    this.successTimeoutId = setTimeout(() => this.successMessage.set(null), TOAST_DURATION_MS);
  }

  private showError(message: string): void {
    this.errorMessage.set(message);
    clearTimeout(this.errorTimeoutId);
    this.errorTimeoutId = setTimeout(() => this.errorMessage.set(null), TOAST_DURATION_MS);
  }

  private loadTransfers(): void {
    this.loading.set(true);
    this.transferApi.list().subscribe({
      next: (transfers) => {
        this.transfers.set(transfers);
        this.loading.set(false);
      },
      error: () => {
        this.showError('Não foi possível carregar os agendamentos.');
        this.loading.set(false);
      },
    });
  }

  private extractErrorMessage(error: HttpErrorResponse): string {
    const apiError = error.error as ApiErrorResponse | undefined;
    return apiError?.message ?? 'Não foi possível agendar a transferência.';
  }
}
