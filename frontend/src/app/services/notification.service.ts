import { Injectable, NgZone } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private eventSource: EventSource | null = null;
  private notificationSubject = new Subject<any>();
  public notifications$ = this.notificationSubject.asObservable();

  constructor(private zone: NgZone, private api: ApiService) {}

  subscribe(userId: number) {
    if (this.eventSource) {
      this.eventSource.close();
    }

    this.eventSource = new EventSource(`http://localhost:8080/api/v1/notifications/subscribe/user/${userId}`);

    this.eventSource.addEventListener('notification', (event: any) => {
      this.zone.run(() => {
        const notification = JSON.parse(event.data);
        this.notificationSubject.next(notification);
        this.playNotificationSound();
        this.showToast(notification); // Creative: Show a toast automatically
      });
    });

    this.eventSource.addEventListener('ping', () => {
      console.log('SSE connected');
    });

    this.eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      this.eventSource?.close();
      // Logic to reconnect if needed
      setTimeout(() => this.subscribe(userId), 5000);
    };
  }

  playNotificationSound() {
    const audio = new Audio('test.mp3');
    audio.play().catch(e => console.log('Audio play blocked or unavailable', e));
  }

  showToast(notification: any) {
    // This is a placeholder; usually you'd use a UI library like Toastr or MatSnackBar
    console.log(`New Notification: ${notification.message}`);
  }

  disconnect() {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }
}
