/*
 * FILE: navbar.ts | LOCATION: frontend/src/app/components/navbar/
 * PURPOSE: Navigation bar component shown on all authenticated pages.
 *          Shows different navigation links for Users vs Admins.
 *          Template: navbar.html | Styles: navbar.scss
 *
 * FOR USERS: Dashboard, Quizzes, Policies, My Policies, Savings, Badges, Achievements, Rewards, Leaderboard
 * FOR ADMINS: Users, Quizzes, Policies, Badges, Rewards, Discount Rules
 *
 * FEATURES: User avatar with initials, profile dropdown, logout button
 * USES: AuthService (services/auth.service.ts), Router (for logout redirect)
 */
import { Component, OnInit, HostListener } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss']
})
export class NavbarComponent implements OnInit {
  notifications: any[] = [];
  unreadCount = 0;
  showNotifications = false;

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    this.showNotifications = false;
  }

  constructor(
    public auth: AuthService, 
    private api: ApiService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    if (this.auth.isLoggedIn()) {
      this.loadNotifications();
      
      // Subscribe to real-time notification stream
      this.notificationService.notifications$.subscribe(notification => {
        // Add new notification to the top of the list
        this.notifications = [notification, ...this.notifications];
        if (!notification.read && !notification.isRead) {
          this.unreadCount++;
        }
      });
    }
  }

  loadNotifications() {
    const userId = this.auth.getUserId();
    if (!userId) return;

    this.api.getNotifications(userId).subscribe({
      next: (data) => {
        this.notifications = data;
        this.unreadCount = data.filter(n => !(n.read || n.isRead)).length;
      }
    });
  }

  toggleNotifications(event: Event) {
    event.stopPropagation();
    this.showNotifications = !this.showNotifications;
  }

  handleNotificationClick(notification: any) {
    this.api.markNotificationAsRead(notification.id).subscribe();
    notification.read = true;
    this.unreadCount = Math.max(0, this.unreadCount - 1);
    this.showNotifications = false;

    if (notification.targetUrl) {
      if (notification.relatedId && !notification.targetUrl.includes('?')) {
        // Append ID if needed, though targetUrl usually includes it or is a list page
        this.router.navigate([notification.targetUrl]);
      } else {
        this.router.navigate([notification.targetUrl]);
      }
    }
  }

  markAllAsRead() {
    const userId = this.auth.getUserId();
    if (!userId) return;

    this.api.markAllNotificationsAsRead(userId).subscribe(() => {
      this.notifications.forEach(n => n.read = true);
      this.unreadCount = 0;
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  get userName(): string {
    return this.auth.getUser()?.name || '';
  }

  get userEmail(): string {
    return this.auth.getUser()?.email || '';
  }

  get isAdmin(): boolean {
    return this.auth.isAdmin();
  }

  get isUnderwriter(): boolean {
    return this.auth.getUser()?.role === 'ROLE_UNDERWRITER';
  }

  get isClaimsOfficer(): boolean {
    return this.auth.getUser()?.role === 'ROLE_CLAIMS_OFFICER';
  }

  get userPoints(): number {
    return 0; // Fetched from API or user state in full implementation
  }

  getInitials(): string {
    const name = this.userName;
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}
