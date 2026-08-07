export interface CreateTransferRequest {
  sourceAccount: string;
  destinationAccount: string;
  amount: number;
  transferDate: string;
}

export interface TransferResponse {
  id: number;
  sourceAccount: string;
  destinationAccount: string;
  amount: number;
  fee: number;
  totalAmount: number;
  transferDate: string;
  schedulingDate: string;
  status: string;
}

export interface ApiErrorResponse {
  code: string;
  message: string;
  details?: string[];
}
