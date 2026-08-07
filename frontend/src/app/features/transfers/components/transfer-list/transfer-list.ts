import { DecimalPipe } from '@angular/common';
import { Component, input } from '@angular/core';

import { TransferResponse } from '../../models/transfer.model';

@Component({
  selector: 'app-transfer-list',
  imports: [DecimalPipe],
  templateUrl: './transfer-list.html',
  styleUrl: './transfer-list.scss',
})
export class TransferList {
  transfers = input<TransferResponse[]>([]);
}
