import { Component, input, output } from '@angular/core';

export type ToastVariant = 'success' | 'danger';

@Component({
  selector: 'app-toast-notification',
  imports: [],
  templateUrl: './toast-notification.html',
  styleUrl: './toast-notification.scss',
})
export class ToastNotification {
  message = input<string | null>(null);
  variant = input<ToastVariant>('success');
  dismissed = output<void>();

  icon(): string {
    return this.variant() === 'success' ? '✓' : '⚠';
  }
}
