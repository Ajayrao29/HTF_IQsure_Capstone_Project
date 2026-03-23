import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})
export class ProfileComponent implements OnInit {
  user: any = {};
  editMode = false;
  loading = false;
  message = '';
  error = '';
  
  // For password change
  passwords = {
    current: '',
    new: '',
    confirm: ''
  };

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getProfile(userId).subscribe({
        next: (data) => {
          this.user = data;
        },
        error: (err) => {
          this.error = 'Failed to load profile details.';
        }
      });
    }
  }

  toggleEdit() {
    this.editMode = !this.editMode;
    if (!this.editMode) {
      this.loadProfile(); // Reset changes if cancel
    }
  }

  saveProfile() {
    this.loading = true;
    this.error = '';
    this.message = '';

    const userId = this.auth.getUserId();
    if (!userId) return;

    this.api.updateProfile(userId, this.user).subscribe({
      next: (updatedUser) => {
        this.loading = false;
        this.editMode = false;
        this.user = updatedUser;
        this.message = 'Profile updated successfully!';
        
        // Update local session info
        const sessionUser = this.auth.getUser();
        if (sessionUser) {
          sessionUser.name = updatedUser.name;
          this.auth.saveUser(sessionUser);
        }

        setTimeout(() => this.message = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Failed to update profile.';
      }
    });
  }

  changePassword() {
    if (!this.passwords.new || this.passwords.new !== this.passwords.confirm) {
      this.error = 'New passwords do not match';
      return;
    }

    if (this.passwords.new.length < 6) {
      this.error = 'Password must be at least 6 characters';
      return;
    }

    this.loading = true;
    this.error = '';
    
    const userId = this.auth.getUserId();
    if (!userId) return;

    // We reuse updateProfile for password change in this implementation
    this.api.updateProfile(userId, { ...this.user, password: this.passwords.new }).subscribe({
      next: () => {
        this.loading = false;
        this.message = 'Password changed successfully!';
        this.passwords = { current: '', new: '', confirm: '' };
        setTimeout(() => this.message = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Failed to change password.';
      }
    });
  }

  getInitials(): string {
    const name = this.user.name;
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}
