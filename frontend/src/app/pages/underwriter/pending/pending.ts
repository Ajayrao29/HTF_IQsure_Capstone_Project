import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';

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

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
    this.loading = true;
    this.notification = null;
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getUnderwriterPoliciesByStatus(userId, 'UNDER_EVALUATION').subscribe(apps => {
        this.applications = apps;
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }

  openQuoteModal(app: any): void {
    this.selectedApp = app;
    this.quoteAmount = app.finalPremium; // Start with the price reflecting rewards/discounts
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

  getFileUrl(path: string): string {
    if (!path) return '';
    if (path.startsWith('http')) return path;
    
    // Clean the path - remove any leading / or /uploads/ prefix we might have saved
    let cleanPath = path;
    if (cleanPath.startsWith('/')) cleanPath = cleanPath.substring(1);
    if (cleanPath.startsWith('uploads/')) cleanPath = cleanPath.substring(8);
    
    return `http://localhost:8080/api/v1/files/view/${cleanPath}`;
  }
}
