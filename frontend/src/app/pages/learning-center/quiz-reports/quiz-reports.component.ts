import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { AttemptResponse } from '../../../models/models';

@Component({
  selector: 'app-quiz-reports',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './quiz-reports.html',
  styleUrls: ['./quiz-reports.scss']
})
export class QuizReportsComponent implements OnInit {
  attempts: AttemptResponse[] = [];
  loading = true;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit() {
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getAttemptsByUser(userId).subscribe({
        next: (data) => {
          this.attempts = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load attempts', err);
          this.loading = false;
        }
      });
    } else {
      this.loading = false;
    }
  }
}
