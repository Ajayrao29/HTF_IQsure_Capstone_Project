import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { UserPolicy } from '../../../models/models';

@Component({
  selector: 'app-underwriter-pending',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe, RouterLink],
  templateUrl: './pending.html',
  styleUrls: ['./pending.scss']
})
export class UnderwriterPendingComponent implements OnInit {
  applications: UserPolicy[] = [];
  loading = true;
  selectedApp: any = null;
  quoteAmount: number | null = null;
  remarks = '';
  submitting = false;
  analyzingAi = false;
  aiResult: any = null;
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
      this.api.getUnderwriterPoliciesByStatus(userId, 'UNDER_EVALUATION').subscribe((apps: UserPolicy[]) => {
        this.applications = apps;
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }

  openQuoteModal(app: any): void {
    this.selectedApp = app;
    this.quoteAmount = app.finalPremium; 
    this.remarks = '';
    this.aiResult = null; // Clear previous AI results
  }

  runAiAnalysis(): void {
    if (!this.selectedApp) return;
    this.analyzingAi = true;
    this.api.getAiAnalysis(this.selectedApp.id).subscribe({
      next: (res) => {
        this.aiResult = res;
        this.analyzingAi = false;
        // Optionally prepend reasoning to remarks
        if (!this.remarks) {
          this.remarks = `[AI Analysis]: ${res.aiReasoningSummary}\n\n[Action]: ${res.personalRecommendation}`;
        }
      },
      error: () => {
        this.analyzingAi = false;
        this.showNotification('AI analysis service currently unavailable.', 'error');
      }
    });
  }

  applyAiSuggestion(): void {
    if (!this.aiResult) return;
    this.quoteAmount = this.aiResult.suggestedQuoteAmount;
    this.remarks = `${this.aiResult.underwritingMemo}\n\n[Verdict]: Adopted AI recommendation at ₹${this.aiResult.suggestedQuoteAmount}`;
    this.showNotification('Actuarial recommendation adopted.', 'success');
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

  rejectApplication(): void {
    if (!this.selectedApp) return;
    if (!this.remarks || this.remarks.length < 10) {
      this.showNotification('Please provide a detailed rejection reason (min 10 chars).', 'error');
      return;
    }

    this.submitting = true;
    this.api.rejectPolicy(this.selectedApp.id, this.remarks).subscribe({
      next: () => {
        this.submitting = false;
        this.selectedApp = null;
        this.showNotification('Application REJECTED and customer notified.', 'success');
        this.loadPending();
      },
      error: () => {
        this.submitting = false;
        this.showNotification('Failed to process rejection. Try again.', 'error');
      }
    });
  }

  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification = { message, type };
    setTimeout(() => {
      this.notification = null;
    }, 4000);
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
