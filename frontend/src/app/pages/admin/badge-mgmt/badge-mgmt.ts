/*
 * FILE: badge-mgmt.ts | LOCATION: pages/admin/badge-mgmt/
 * PURPOSE: Admin Badge Management page (URL: /admin/badge-mgmt). Admin can:
 *          - Create badges with a name, description, and required points threshold
 *          - Delete badges
 * TEMPLATE: badge-mgmt.html | STYLES: badge-mgmt.scss
 * CALLS: api.service.ts → getAllBadges(), createBadge(), deleteBadge() → BadgeController
 * GUARD: AdminGuard — only accessible by admin users
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-badge-mgmt', standalone: true, imports: [CommonModule, FormsModule], templateUrl: './badge-mgmt.html', styleUrls: ['./badge-mgmt.scss'] })
export class BadgeMgmtComponent implements OnInit {
  badges: any[] = []; loading = true; showForm = false; editingBadge: any = null;
  form: any = { name: '', description: '', reqPoints: null };
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.getAllBadges().subscribe(b => { this.badges = b; this.loading = false; }); }
  openEdit(b: any): void { this.editingBadge = b; this.form = { name: b.name, description: b.description, reqPoints: b.reqPoints }; this.showForm = true; }
  save(): void {
    if (this.editingBadge) {
      this.api.updateBadge(this.editingBadge.badgeId, this.form).subscribe(updated => {
        const i = this.badges.findIndex(b => b.badgeId === this.editingBadge.badgeId);
        this.badges[i] = updated;
        this.editingBadge = null; this.form = { name: '', description: '', reqPoints: null }; this.showForm = false;
      });
    } else {
      this.api.createBadge(this.form).subscribe(b => {
        this.badges.push(b); this.form = { name: '', description: '', reqPoints: null }; this.showForm = false;
      });
    }
  }
  delete(id: number): void { if (!confirm('Delete badge?')) return; this.api.deleteBadge(id).subscribe(() => this.badges = this.badges.filter(b => b.badgeId !== id)); }

  getBadgeIcon(index: number): string {
    const icons = ['\u2606', '\u2736', '\u2666', '\u265B', '\u2605', '\u2764'];
    return icons[index % icons.length];
  }

  getBadgeLevel(index: number): string {
    const levels = ['bronze', 'silver', 'gold', 'diamond', 'platinum', 'ruby'];
    return levels[index % levels.length];
  }
}
