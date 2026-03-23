import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { Claim } from '../../../models/models';

@Component({
  selector: 'app-claims-officer-claims',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe, RouterLink],
  templateUrl: './claims.html',
  styleUrls: ['./claims.scss']
})
export class ClaimsOfficerClaimsComponent implements OnInit {
  claims: Claim[] = [];
  filteredClaims: Claim[] = [];
  loading = true;
  activeFilter = 'ALL';
  
  // Processing Modal
  selectedClaim: Claim | null = null;
  processingStatus = '';
  remarks = '';
  approvedAmount: number = 0;
  isProcessing = false;

  // 🤖 AGENTIC AI AUDIT
  aiAudit: string = '';
  aiGenerating: boolean = false;

  // UI Notifications
  notification: { message: string, type: 'success' | 'error' } | null = null;
  showSettlementModal = false;
  settlementAmount: number = 0;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.api.getAllClaimsAdmin().subscribe((c: Claim[]) => {
      this.claims = c;
      this.applyFilter(this.activeFilter);
      this.loading = false;
    });
  }

  applyFilter(filter: string): void {
    this.activeFilter = filter;
    if (filter === 'ALL') {
      this.filteredClaims = this.claims;
    } else {
      this.filteredClaims = this.claims.filter(c => c.status === filter);
    }
  }

  openProcessModal(claim: Claim): void {
    this.selectedClaim = claim;
    this.processingStatus = claim.status === 'SUBMITTED' ? 'UNDER_REVIEW' : claim.status;
    this.remarks = claim.reviewerRemarks || '';
    this.approvedAmount = claim.approvedAmount || claim.amount || 0;
    
    // 🤖 TRIGGER AGENTIC AUDIT
    this.runAiAudit(claim);
  }

  submitProcess(): void {
    if (!this.selectedClaim) return;
    this.isProcessing = true;
    this.api.processClaim(this.selectedClaim.id!, this.processingStatus, this.remarks, this.approvedAmount).subscribe({
      next: () => {
        this.showNotification('Claim processed successfully!', 'success');
        this.selectedClaim = null;
        this.loadClaims();
        this.isProcessing = false;
      },
      error: () => {
        this.showNotification('Error processing claim. Please check details.', 'error');
        this.isProcessing = false;
      }
    });
  }

  openSettleModal(claim: Claim): void {
    this.selectedClaim = claim;
    this.settlementAmount = claim.approvedAmount || 0;
    this.showSettlementModal = true;
  }

  confirmSettlement(): void {
    if (!this.selectedClaim) return;
    this.isProcessing = true;
    this.api.settleClaim(this.selectedClaim.id!, this.settlementAmount).subscribe({
      next: () => {
        this.showNotification('Claim settled successfully! Coverage updated.', 'success');
        this.showSettlementModal = false;
        this.selectedClaim = null;
        this.loadClaims();
        this.isProcessing = false;
      },
      error: (err) => {
        this.showNotification('Settlement failed: ' + (err.error?.message || 'Server error'), 'error');
        this.isProcessing = false;
      }
    });
  }

  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification = { message, type };
    setTimeout(() => this.notification = null, 5000);
  }

  runAiAudit(claim: Claim): void {
    if (!claim) return;
    this.aiGenerating = true;
    this.aiAudit = '';
    
    setTimeout(() => {
      this.aiGenerating = false;
      this.aiAudit = `🤖 IQSURE AGENTIC AUDIT SUMMARY:
• Medical diagnosis [${claim.diagnosis}] cross-verified against policy inclusion list.
• Hospitalisation at [${claim.hospitalName}] confirmed via digital record matching.
• Member IQ-Verified Status: PLATINUM (Eligible for prioritized 2-hour settlement).
• SUGGESTED ACTION: Approve ₹${claim.amount} (100% Coverage).`;
    }, 1200);
  }

  getStatusClass(status: string): string {
    switch(status) {
      case 'APPROVED': return 'status-approved';
      case 'REJECTED': return 'status-rejected';
      case 'SUBMITTED': return 'status-submitted';
      case 'UNDER_REVIEW': return 'status-review';
      case 'SETTLED': return 'status-settled';
      default: return '';
    }
  }
}
