import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { Badge } from '../../../models/models';

/**
 * Admin component for managing user badges and thresholds.
 * Best Practice: Use typed models and clean separation of concerns.
 */
@Component({
  selector: 'app-badge-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './badge-mgmt.html',
  styleUrls: ['./badge-mgmt.scss']
})
export class BadgeMgmtComponent implements OnInit {
  badges: Badge[] = [];
  loading = true;
  showForm = false;
  editingBadge: Badge | null = null;
  
  form: Partial<Badge> = this.getEmptyForm();

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadBadges();
  }

  /** Reload the full list of badges from the API */
  loadBadges(): void {
    this.loading = true;
    this.api.getAllBadges().subscribe({
      next: (data) => {
        this.badges = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load badges:', err);
        this.loading = false;
      }
    });
  }

  /** Open the form for creating a new badge */
  openCreate(): void {
    this.editingBadge = null;
    this.form = this.getEmptyForm();
    this.showForm = true;
  }

  /** Open the form for editing an existing badge */
  openEdit(badge: Badge): void {
    this.editingBadge = badge;
    this.form = { ...badge };
    this.showForm = true;
  }

  /** Save the new or updated badge data */
  save(): void {
    this.loading = true;
    const request = this.editingBadge 
      ? this.api.updateBadge(this.editingBadge.badgeId, this.form)
      : this.api.createBadge(this.form);

    request.subscribe({
      next: () => {
        this.showForm = false;
        this.loadBadges();
      },
      error: (err) => {
        console.error('Failed to save badge:', err);
        this.loading = false;
        alert('Could not save badge. Check console for details.');
      }
    });
  }

  /** Permanent deletion of a badge */
  delete(id: number): void {
    if (!confirm('Are you sure you want to delete this badge? This cannot be undone.')) {
      return;
    }

    this.api.deleteBadge(id).subscribe({
      next: () => this.loadBadges(),
      error: (err) => console.error('Failed to delete badge:', err)
    });
  }

  getBadgeIcon(index: number): string {
    const icons = ['\u2606', '\u2736', '\u2666', '\u265B', '\u2605', '\u2764'];
    return icons[index % icons.length];
  }

  getBadgeLevel(index: number): string {
    const levels = ['bronze', 'silver', 'gold', 'diamond', 'platinum', 'ruby'];
    return levels[index % levels.length];
  }

  private getEmptyForm(): Partial<Badge> {
    return { name: '', description: '', reqPoints: 0, icon: '' };
  }
}
