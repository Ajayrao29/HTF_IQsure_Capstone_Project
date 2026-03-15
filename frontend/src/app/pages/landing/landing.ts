/*
 * FILE: landing.ts | LOCATION: pages/landing/
 * PURPOSE: Landing/Home page (URL: /). Public page shown before login.
 *          Displays feature highlights and stats about the platform.
 * TEMPLATE: landing.html | STYLES: landing.scss
 * ROUTE: { path: '', component: LandingComponent } in app.routes.ts
 */
import { Component } from '@angular/core';

import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.html',
  styleUrls: ['./landing.scss']
})
export class LandingComponent {
  features = [
    { icon: '🎓', title: 'Learn Insurance', desc: 'Interactive quizzes about Health, Life, and Motor insurance' },
    { icon: '⭐', title: 'Earn Points', desc: 'Get 10 points per correct answer with speed bonuses' },
    { icon: '🏆', title: 'Unlock Badges', desc: 'Collect 4 progressive achievement badges' },
    { icon: '💰', title: 'Save Money', desc: 'Get up to 50% discount on real insurance premiums' }
  ];

  stats = [
    { value: '8', label: 'Achievements' },
    { value: '50%', label: 'Max Discount' },
    { value: '3', label: 'Quiz Categories' },
    { value: '∞', label: 'Levels' }
  ];
}
