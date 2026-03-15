/*
 * FILE: quizzes.ts | LOCATION: pages/quizzes/
 * PURPOSE: Quiz list page (URL: /quizzes). Shows all available quizzes.
 *          User clicks "Take Quiz" → navigates to /take-quiz/:id.
 * TEMPLATE: quizzes.html | STYLES: quizzes.scss
 * CALLS: api.service.ts → getAllQuizzes() → QuizController → GET /api/v1/quizzes
 */
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-quizzes', standalone: true, imports: [CommonModule], templateUrl: './quizzes.html', styleUrls: ['./quizzes.scss'] })
export class QuizzesComponent implements OnInit {
  quizzes: any[] = []; loading = true;
  constructor(private api: ApiService, private router: Router) {}
  ngOnInit(): void { this.api.getAllQuizzes().subscribe(q => { this.quizzes = q; this.loading = false; }); }
  takeQuiz(quizId: number): void { this.router.navigate(['/take-quiz', quizId]); }
  getDifficultyClass(diff: string): string { return diff === 'EASY' ? 'easy' : diff === 'MEDIUM' ? 'medium' : 'hard'; }
}
