import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { User } from '../../../models/models';

@Component({
  selector: 'app-manage-claims-officers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './claims-officers.html',
  styleUrls: ['./claims-officers.scss']
})
export class ManageClaimsOfficersComponent implements OnInit {
  officers: User[] = [];
  loading = true;
  showForm = false;

  // Form model
  newOfficer = {
    name: '',
    email: '',
    password: '',
    employeeId: '',
    department: 'CLAIMS',
    approvalLimit: 500000
  };

  error = '';
  success = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadOfficers();
  }

  loadOfficers(): void {
    this.loading = true;
    this.api.getUsersByRole('ROLE_CLAIMS_OFFICER').subscribe(users => {
      this.officers = users;
      this.loading = false;
    });
  }

  addOfficer(): void {
    this.error = '';
    this.success = '';
    
    if(!this.newOfficer.name || !this.newOfficer.email || !this.newOfficer.password || !this.newOfficer.employeeId) {
        this.error = 'Please fill all required fields';
        return;
    }

    this.api.createClaimsOfficer(this.newOfficer).subscribe({
      next: (res) => {
        this.officers.push(res);
        this.showForm = false;
        this.success = 'Claims Officer added successfully!';
        this.resetForm();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to add officer';
      }
    });
  }

  resetForm(): void {
    this.newOfficer = {
      name: '',
      email: '',
      password: '',
      employeeId: '',
      department: 'CLAIMS',
      approvalLimit: 500000
    };
  }

  deleteOfficer(id: number): void {
    if(confirm('Remove this officer?')) {
      this.api.deleteUser(id).subscribe(() => {
        this.officers = this.officers.filter(o => o.userId !== id);
      });
    }
  }
}
