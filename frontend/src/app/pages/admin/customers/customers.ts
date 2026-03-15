import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../services/api.service';
import { User } from '../../../models/models';

@Component({
  selector: 'app-manage-customers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customers.html',
  styleUrls: ['./customers.scss']
})
export class ManageCustomersComponent implements OnInit {
  customers: User[] = [];
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.api.getUsersByRole('ROLE_USER').subscribe(users => {
      this.customers = users;
      this.loading = false;
    });
  }

  updateStatus(user: User, status: string): void {
    this.api.updateUserStatus(user.userId, status).subscribe(updated => {
      user.status = updated.status;
    });
  }

  deleteCustomer(userId: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.api.deleteUser(userId).subscribe(() => {
        this.customers = this.customers.filter(c => c.userId !== userId);
      });
    }
  }
}
