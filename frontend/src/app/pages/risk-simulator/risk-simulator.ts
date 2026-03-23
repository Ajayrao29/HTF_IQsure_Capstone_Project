/*
 * FILE: risk-simulator.ts | LOCATION: pages/risk-simulator/
 * PURPOSE: Risk Awareness Simulator page.
 *          User selects a REAL active policy from the platform, enters a custom scenario
 *          (e.g., "Car crash", "House fire") and the estimated cost.
 *          The simulator calculates how much the selected policy would cover
 *          (up to its coverageAmount) vs paying entirely out of pocket.
 * TEMPLATE: risk-simulator.html | STYLES: risk-simulator.scss
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

interface SimulationResult {
  policy: any;
  scenarioName: string;
  eventCost: number;
  insuranceCovers: number;
  outOfPocket: number;
  annualPremium: number;
  totalWithInsurance: number;
  savings: number;
  savingsPercent: number;
}

@Component({
  selector: 'app-risk-simulator',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './risk-simulator.html',
  styleUrls: ['./risk-simulator.scss']
})
export class RiskSimulatorComponent implements OnInit {

  policies: any[] = [];
  loading = true;

  /* ── Form state ── */
  selectedPolicyId: number | '' = '';
  scenarioName = '';
  eventCost: number | null = null;

  /* ── Result state ── */
  result: SimulationResult | null = null;
  showResult = false;
  animateNumbers = false;
  simulationsRun = 0;

  constructor(private api: ApiService, public auth: AuthService) {}

  ngOnInit(): void {
    this.api.getActivePolicies().subscribe({
      next: (policies) => {
        this.policies = policies;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  /* ── Run the simulation ── */
  simulate(): void {
    if (!this.isFormValid) return;

    const policy = this.policies.find(p => p.policyId === Number(this.selectedPolicyId));
    if (!policy) return;

    const cost = this.eventCost!;
    
    // Insurance covers up to the maximum coverage amount of the policy
    const insuranceCovers = Math.min(cost, policy.coverageAmount);
    
    // What you pay out of pocket for the event (if cost exceeds coverage)
    const outOfPocket = cost - insuranceCovers;
    
    // The cost of having the insurance
    const annualPremium = policy.basePremium;
    
    // Total cost with insurance = what you pay for the event + what you paid for the insurance
    const totalWithInsurance = outOfPocket + annualPremium;
    
    // How much you saved compared to having no insurance at all
    const savings = cost - totalWithInsurance;
    const savingsPercent = cost > 0 ? Math.round(Math.max(0, savings / cost) * 1000) / 10 : 0;

    this.result = {
      policy,
      scenarioName: this.scenarioName || 'Unexpected Disaster',
      eventCost: cost,
      insuranceCovers,
      outOfPocket,
      annualPremium,
      totalWithInsurance,
      savings,
      savingsPercent
    };

    this.showResult = false;
    this.animateNumbers = false;

    setTimeout(() => {
      this.showResult = true;
      this.simulationsRun++;
    }, 100);

    setTimeout(() => {
      this.animateNumbers = true;
    }, 600);
  }

  /* ── Reset to try another scenario ── */
  resetSimulation(): void {
    this.showResult = false;
    this.animateNumbers = false;
    setTimeout(() => {
      this.result = null;
      // Keep form values so they can easily tweak the cost or policy and re-run
    }, 300);
  }

  /* ── Helpers ── */
  formatCurrency(amount: number): string {
    return '₹' + amount.toLocaleString('en-IN', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  get isFormValid(): boolean {
    return this.selectedPolicyId !== '' && !!this.eventCost && this.eventCost > 0;
  }

  getPolicyIcon(type: string): string {
    switch (type?.toUpperCase()) {
      case 'HEALTH': return '🏥';
      case 'LIFE': return '💼';
      case 'AUTO': return '🚗';
      case 'HOME': return '🏠';
      case 'DENTAL': return '🦷';
      default: return '🛡️';
    }
  }

  get activePolicy() {
    if (this.selectedPolicyId === '') return null;
    return this.policies.find(p => p.policyId === Number(this.selectedPolicyId)) || null;
  }
}
