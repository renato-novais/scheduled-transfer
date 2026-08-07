import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, input } from '@angular/core';

import { TransferResponse } from '../../models/transfer.model';

const STATUS_LABELS: Record<string, string> = {
  SCHEDULED: 'Agendado',
};

const STATUS_BADGE_CLASSES: Record<string, string> = {
  SCHEDULED: 'text-bg-success',
};

@Component({
  selector: 'app-transfer-list',
  imports: [DecimalPipe, DatePipe],
  templateUrl: './transfer-list.html',
  styleUrl: './transfer-list.scss',
})
export class TransferList {
  transfers = input<TransferResponse[]>([]);

  statusLabel(status: string): string {
    return STATUS_LABELS[status] ?? status;
  }

  statusBadgeClass(status: string): string {
    return STATUS_BADGE_CLASSES[status] ?? 'text-bg-secondary';
  }
}
