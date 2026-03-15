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

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
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
    this.api.sendQuote(this.selectedApp.id, this.quoteAmount, this.remarks).subscribe(() => {
      this.submitting = false;
      this.selectedApp = null;
      this.loadPending();
    });
  }
}
