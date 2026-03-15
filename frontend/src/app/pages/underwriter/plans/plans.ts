import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Policy } from '../../../models/models';

@Component({
  selector: 'app-underwriter-plans',
  standalone: true,
  imports: [CommonModule, DecimalPipe, RouterLink],
  templateUrl: './plans.html',
  styleUrls: ['./plans.scss']
})
export class UnderwriterPlansComponent implements OnInit {
  plans: Policy[] = [];
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getAllPolicies().subscribe(p => {
      this.plans = p;
      this.loading = false;
    });
  }

  getCoverageInLakhs(amount: number): string {
    return (amount / 100000).toFixed(1) + ' L';
  }
}
