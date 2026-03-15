import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { User } from '../../../models/models';

@Component({
  selector: 'app-manage-underwriters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './underwriters.html',
  styleUrls: ['./underwriters.scss']
})
export class ManageUnderwritersComponent implements OnInit {
  underwriters: User[] = [];
  loading = true;
  showForm = false;

  // Form model
  newUW = {
    name: '',
    email: '',
    password: '',
    licenseNumber: '',
    specialization: 'HEALTH',
    commissionPercentage: 5.0
  };

  error = '';
  success = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadUnderwriters();
  }

  loadUnderwriters(): void {
    this.loading = true;
    this.api.getUsersByRole('ROLE_UNDERWRITER').subscribe(users => {
      this.underwriters = users;
      this.loading = false;
    });
  }

  addUnderwriter(): void {
    this.error = '';
    this.success = '';
    
    if(!this.newUW.name || !this.newUW.email || !this.newUW.password || !this.newUW.licenseNumber) {
        this.error = 'Please fill all required fields';
        return;
    }

    this.api.createUnderwriter(this.newUW).subscribe({
      next: (res) => {
        this.underwriters.push(res);
        this.showForm = false;
        this.success = 'Underwriter added successfully!';
        this.resetForm();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to add underwriter';
      }
    });
  }

  resetForm(): void {
    this.newUW = {
      name: '',
      email: '',
      password: '',
      licenseNumber: '',
      specialization: 'HEALTH',
      commissionPercentage: 5.0
    };
  }

  deleteUnderwriter(id: number): void {
    if(confirm('Delete this underwriter?')) {
      this.api.deleteUser(id).subscribe(() => {
        this.underwriters = this.underwriters.filter(u => u.userId !== id);
      });
    }
  }
}
