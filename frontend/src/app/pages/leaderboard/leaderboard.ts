/*
 * FILE: leaderboard.ts | LOCATION: pages/leaderboard/
 * PURPOSE: Leaderboard page (URL: /leaderboard). Shows top users ranked by total points.
 * TEMPLATE: leaderboard.html | STYLES: leaderboard.scss
 * CALLS: api.service.ts → getLeaderboard() → UserController → GET /api/v1/users/leaderboard
 * BACKEND: UserService.getLeaderboard() → queries users sorted by points DESC
 */
import { Component, OnInit } from '@angular/core';

import { ApiService } from '../../services/api.service';

@Component({ selector: 'app-leaderboard', standalone: true, imports: [], templateUrl: './leaderboard.html', styleUrls: ['./leaderboard.scss'] })
export class LeaderboardComponent implements OnInit {
  entries: any[] = [];
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getLeaderboard().subscribe(data => { this.entries = data; this.loading = false; });
  }

  getMedalIcon(rank: number): string {
    return rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : '#' + rank;
  }
}
