/*
 * FILE: my-policies.ts | LOCATION: pages/my-policies/
 * PURPOSE: My Policies page (URL: /my-policies). Shows all policies the user has purchased.
 * CALLS: api.service.ts → getUserPolicies() → UserPolicyController → GET /api/v1/users/{id}/policies
 */
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-my-policies', standalone: true, imports: [CommonModule, RouterLink, DecimalPipe], templateUrl: './my-policies.html', styleUrls: ['./my-policies.scss'] })
export class MyPoliciesComponent implements OnInit {
  policies: any[] = []; 
  loading = true;
  
  // UI States
  showPaymentConfirm = false;
  showSuccessCard = false;
  selectedPolicy: any = null;
  paymentNotice: { message: string, type: 'success' | 'error' } | null = null;
  processing = false;
  showCertificate = false;
  
  // 🛡️ MEMBER IDENTITY (AGENTIC EXPERIENCE)
  viewCertificate(policy: any): void {
    this.selectedPolicy = policy;
    this.showCertificate = true;
  }

  getPolicyNumber(id: number): string {
    return 'IQ-' + (1000 + id) + '-' + (100000 + id * 7).toString().substring(0, 4);
  }

  constructor(private api: ApiService, private auth: AuthService) {}
  ngOnInit(): void { this.loadPolicies(); }

  loadPolicies(): void {
    this.api.getUserPolicies(this.auth.getUserId()!).subscribe(p => { 
      this.policies = p; 
      this.loading = false; 
    });
  }

  getPolicyIcon(type: string): string { return type === 'HEALTH' ? '❤️' : type === 'LIFE' ? '🌿' : '🚗'; }
  
  getStatusClass(status: string): string { 
    switch(status) {
      case 'ACTIVE': return 'active';
      case 'PENDING_UNDERWRITING': return 'pending';
      case 'UNDER_EVALUATION': return 'evaluating';
      case 'QUOTES_SENT': return 'quote-sent';
      case 'EXPIRED': return 'expired';
      case 'REJECTED': return 'rejected';
      default: return 'cancelled';
    }
  }

  payNow(policy: any): void {
    this.selectedPolicy = policy;
    this.showPaymentConfirm = true;
    this.paymentNotice = null;
  }

  confirmPayment(): void {
    if (!this.selectedPolicy) return;
    this.processing = true;
    this.showPaymentConfirm = false;

    this.api.payPolicy(this.auth.getUserId()!, this.selectedPolicy.id).subscribe({
      next: () => {
        this.processing = false;
        this.showSuccessCard = true;
        this.playSuccessSound();
        this.loadPolicies();
        // Hide success card after 4 seconds
        setTimeout(() => this.showSuccessCard = false, 4000);
      },
      error: (err) => {
        this.processing = false;
        this.showNotification('Payment failed: ' + (err.error?.message || 'Transaction declined'), 'error');
      }
    });
  }

  playSuccessSound(): void {
    try {
      const audioCtx = new (window.AudioContext || (window as any).webkitAudioContext)();
      
      const playTone = (freq: number, start: number, duration: number) => {
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(freq, audioCtx.currentTime + start);
        gain.gain.setValueAtTime(0.1, audioCtx.currentTime + start);
        gain.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + start + duration);
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.start(audioCtx.currentTime + start);
        osc.stop(audioCtx.currentTime + start + duration);
      };

      // Play a pleasant double-chime (E5 -> G5)
      playTone(659.25, 0, 0.5); // E5
      playTone(783.99, 0.15, 0.6); // G5
    } catch (e) {
      console.warn('Audio context not supported');
    }
  }

  showNotification(message: string, type: 'success' | 'error'): void {
    this.paymentNotice = { message, type };
    // Auto-hide after 5 seconds
    setTimeout(() => this.paymentNotice = null, 5000);
  }
}
