import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-underwriter-pending',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe, RouterLink],
  templateUrl: './pending.html',
  styleUrls: ['./pending.scss']
})
export class UnderwriterPendingComponent implements OnInit {
  applications: any[] = [];
  loading = true;
  selectedApp: any = null;
  quoteAmount: number | null = null;
  remarks = '';
  submitting = false;
  notification: { message: string, type: 'success' | 'error' } | null = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
    this.loading = true;
    this.notification = null;
    this.api.getPoliciesByStatus('UNDER_EVALUATION').subscribe(apps => {
      this.applications = apps;
      this.loading = false;
    });
  }

  openQuoteModal(app: any): void {
    this.selectedApp = app;
    this.quoteAmount = app.basePremium; // Start with base premium
    this.remarks = '';
  }

  submitQuote(): void {
    if (!this.quoteAmount || !this.selectedApp) return;
    this.submitting = true;
    this.notification = null;

    this.api.sendQuote(this.selectedApp.id, this.quoteAmount, this.remarks).subscribe({
      next: () => {
        this.submitting = false;
        this.selectedApp = null;
        this.showNotification('Quote sent successfully!', 'success');
        this.loadPending();
      },
      error: () => {
        this.submitting = false;
        this.showNotification('Failed to send quote. Please try again.', 'error');
      }
    });
  }

  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification = { message, type };
    setTimeout(() => {
      this.notification = null;
    }, 3000);
  }
}
