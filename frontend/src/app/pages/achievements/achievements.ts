/*
 * FILE: achievements.ts | LOCATION: pages/achievements/
 * PURPOSE: Achievements page (URL: /achievements). Shows progress, level, and 8 achievement milestones.
 *          Checks user's quiz history, policies, badges, and leaderboard position to unlock achievements.
 * TEMPLATE: achievements.html | STYLES: achievements.scss
 * CALLS: api.service.ts → getProfile(), getAttemptsByUser(), getUserPolicies(), getBadgesByUser(), getLeaderboard()
 * BACKEND: Multiple controllers — UserController, AttemptController, UserPolicyController, BadgeController
 */
import { Component, OnInit } from '@angular/core';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-achievements',
  standalone: true,
  imports: [],
  templateUrl: './achievements.html',
  styleUrls: ['./achievements.scss']
})
export class AchievementsComponent implements OnInit {
  userPoints = 0;
  level = 1;
  levelProgress = 0;
  nextLevelPoints = 100;
  completionRate = 0;
  streak = 0;

  achievements = [
    { icon: '🎓', name: 'First Steps', description: 'Complete your first quiz', points: 10, unlocked: false },
    { icon: '💯', name: 'Perfect Score', description: 'Score 100% on any quiz', points: 50, unlocked: false },
    { icon: '🔥', name: 'On Fire', description: 'Complete 3 quizzes in one day', points: 30, unlocked: false },
    { icon: '📚', name: 'Knowledge Seeker', description: 'Complete all 3 quiz categories', points: 75, unlocked: false },
    { icon: '💎', name: 'Premium Hunter', description: 'Purchase your first policy', points: 100, unlocked: false },
    { icon: '🎯', name: 'Sharpshooter', description: 'Score 80%+ on 5 quizzes', points: 150, unlocked: false },
    { icon: '👑', name: 'Top 10', description: 'Reach top 10 on leaderboard', points: 200, unlocked: false },
    { icon: '🌟', name: 'Badge Collector', description: 'Unlock all badges', points: 250, unlocked: false },
  ];

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId()!;
    this.api.getProfile(userId).subscribe(u => {
      this.userPoints = u.userPoints;
      this.calculateLevel();
      this.checkAchievements(userId);
    });
  }

  calculateLevel(): void {
    this.level = Math.floor(this.userPoints / 100) + 1;
    this.nextLevelPoints = this.level * 100;
    this.levelProgress = ((this.userPoints % 100) / 100) * 100;
  }

  checkAchievements(userId: number): void {
    this.api.getAttemptsByUser(userId).subscribe(attempts => {
      if (attempts.length > 0) this.achievements[0].unlocked = true;
      if (attempts.some(a => a.percentage === 100)) this.achievements[1].unlocked = true;
      if (attempts.filter(a => new Date(a.attemptDate).toDateString() === new Date().toDateString()).length >= 3)
        this.achievements[2].unlocked = true;

      const categories = new Set(attempts.map(a => a.quizTitle.split(' ')[0]));
      if (categories.size >= 3) this.achievements[3].unlocked = true;

      if (attempts.filter(a => a.percentage >= 80).length >= 5) this.achievements[5].unlocked = true;

      this.completionRate = Math.round((attempts.length / 3) * 100);
      
      // Calculate actual consecutive day streak
      let calculatedStreak = 0;
      if (attempts.length > 0) {
        const uniqueDates = [...new Set(attempts.map(a => {
          const d = new Date(a.attemptDate);
          return new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
        }))].sort((a, b) => b - a); // sort descending

        const now = new Date();
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
        const yesterday = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 1).getTime();

        // If the most recent attempt is today or yesterday, streak is active
        if (uniqueDates.length > 0 && (uniqueDates[0] === today || uniqueDates[0] === yesterday)) {
          let expectedDate = new Date(uniqueDates[0]);
          for (const dateTime of uniqueDates) {
            if (dateTime === expectedDate.getTime()) {
              calculatedStreak++;
              expectedDate.setDate(expectedDate.getDate() - 1); // decrement by one day safely
            } else {
              break;
            }
          }
        }
      }
      this.streak = calculatedStreak;
    });

    this.api.getUserPolicies(userId).subscribe(policies => {
      if (policies.length > 0) this.achievements[4].unlocked = true;
    });

    this.api.getBadgesByUser(userId).subscribe(badges => {
      if (badges.length >= 4) this.achievements[7].unlocked = true;
    });

    this.api.getLeaderboard().subscribe(board => {
      const userRank = board.findIndex(e => e.userId === userId) + 1;
      if (userRank > 0 && userRank <= 10) this.achievements[6].unlocked = true;
    });
  }
}
