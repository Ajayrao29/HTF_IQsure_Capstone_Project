/*
 * FILE: badges.ts | LOCATION: pages/badges/
 * PURPOSE: Badges page (URL: /badges). Shows all available badges and highlights which ones user earned.
 * CALLS: api.service.ts → getAllBadges(), getBadgesByUser() → BadgeController
 */
import { Component, OnInit } from '@angular/core';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-badges', standalone: true, imports: [], templateUrl: './badges.html', styleUrls: ['./badges.scss'] })
export class BadgesComponent implements OnInit {
  allBadges: any[] = [];
  myBadgeIds: Set<number> = new Set();
  loading = true;
  constructor(private api: ApiService, private auth: AuthService) {}
  ngOnInit(): void {
    const userId = this.auth.getUserId()!;
    this.api.getAllBadges().subscribe(all => { this.allBadges = all; this.loading = false; });
    this.api.getBadgesByUser(userId).subscribe(mine => mine.forEach((b: any) => this.myBadgeIds.add(b.badgeId)));
  }
  isEarned(badgeId: number): boolean { return this.myBadgeIds.has(badgeId); }
}
