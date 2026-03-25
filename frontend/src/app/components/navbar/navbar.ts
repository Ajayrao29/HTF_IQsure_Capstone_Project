import { Component, OnInit, HostListener } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';

/**
 * Main application navigation and notification center.
 * Best Practice: Role-based navigation and centralized user state.
 */
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

  @HostListener('document:click')
  onDocumentClick() {
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
      this.subscribeToRealtimeStream();
    }
  }

  /** Fetch notification history using the API */
  private loadNotifications() {
    const userId = this.auth.getUserId();
    if (!userId) return;

    this.api.getNotifications(userId).subscribe({
      next: (data) => {
        this.notifications = data;
        this.unreadCount = data.filter(n => !(n.read || n.isRead)).length;
      },
      error: (err) => console.error('Failed to load notifications:', err)
    });
  }

  /** Subscribe to SSE stream for live updates via the shared NotificationService */
  private subscribeToRealtimeStream() {
    this.notificationService.notifications$.subscribe(notification => {
      this.notifications = [notification, ...this.notifications];
      if (!notification.read && !notification.isRead) {
        this.unreadCount++;
      }
    });
  }

  toggleNotifications(event: Event) {
    event.stopPropagation();
    this.showNotifications = !this.showNotifications;
  }

  /** Marks a single notification as read and navigates if targetUrl is present */
  handleNotificationClick(notification: any) {
    this.api.markNotificationAsRead(notification.id).subscribe();
    notification.read = true;
    this.unreadCount = Math.max(0, this.unreadCount - 1);
    this.showNotifications = false;

    if (notification.targetUrl) {
      this.router.navigate([notification.targetUrl]);
    }
  }

  /** Bulk mark all unread notifications as read */
  markAllAsRead() {
    const userId = this.auth.getUserId();
    if (!userId) return;

    this.api.markAllNotificationsAsRead(userId).subscribe({
      next: () => {
        this.notifications.forEach(n => n.read = true);
        this.unreadCount = 0;
      },
      error: (err) => console.error('Failed to clear notifications:', err)
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

  get isAdmin(): boolean { return this.auth.isAdmin(); }

  get isUnderwriter(): boolean {
    return this.auth.getUser()?.role === 'ROLE_UNDERWRITER';
  }

  get isClaimsOfficer(): boolean {
    return this.auth.getUser()?.role === 'ROLE_CLAIMS_OFFICER';
  }

  get userPoints(): number {
    return this.auth.getUser()?.userPoints || 0;
  }

  /** Generates initials for the user profile circle */
  getInitials(): string {
    const name = this.userName;
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    return parts.length >= 2 
      ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
      : name.substring(0, 2).toUpperCase();
  }
}
