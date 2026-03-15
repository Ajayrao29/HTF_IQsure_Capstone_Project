/*
 * FILE: about.ts | LOCATION: pages/about/
 * PURPOSE: About page (URL: /about). Describes the platform's mission and team.
 * TEMPLATE: about.html | STYLES: about.scss
 */
import { Component } from '@angular/core';

import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './about.html',
  styleUrls: ['./about.scss']
})
export class AboutComponent {
  team = [
    { role: 'Platform', icon: '🎮', desc: 'Gamified learning experience' },
    { role: 'Education', icon: '📚', desc: 'Insurance literacy focus' },
    { role: 'Savings', icon: '💰', desc: 'Real premium discounts' },
    { role: 'Technology', icon: '⚡', desc: 'Modern tech stack' }
  ];

  values = [
    { title: 'Education First', desc: 'We believe informed customers make better decisions', icon: '🎓' },
    { title: 'Real Value', desc: 'Actual savings, not just virtual rewards', icon: '💎' },
    { title: 'Transparency', desc: 'Clear pricing and discount breakdowns', icon: '🔍' },
    { title: 'Innovation', desc: 'Gamification meets insurance', icon: '🚀' }
  ];
}
