/*
 * FILE: take-quiz.ts | LOCATION: pages/take-quiz/
 * PURPOSE: Quiz-taking page (URL: /take-quiz/:id). Shows questions one at a time with a 5-min timer.
 *          User selects answers → clicks Submit → sends to backend for scoring → navigates to quiz-result.
 * TEMPLATE: take-quiz.html | STYLES: take-quiz.scss
 * CALLS: api.service.ts → getQuizById(), getQuestionsByQuiz(), submitQuiz()
 * BACKEND: QuizController (quiz info), QuestionController (questions), AttemptController (submit)
 */
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { interval, Subscription } from 'rxjs';

@Component({ selector: 'app-take-quiz', standalone: true, imports: [], templateUrl: './take-quiz.html', styleUrls: ['./take-quiz.scss'] })
export class TakeQuizComponent implements OnInit {
  quiz: any = null; questions: any[] = [];
  selectedAnswers: { [questionId: number]: number } = {};
  loading = true; submitting = false;

  timeLeft = 300; // 5 minutes
  timerSubscription?: Subscription;
  speedBonus = 0;

  constructor(private route: ActivatedRoute, private router: Router, private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const quizId = Number(this.route.snapshot.paramMap.get('id'));
    this.api.getQuizById(quizId).subscribe(q => this.quiz = q);
    this.api.getQuestionsByQuiz(quizId).subscribe(qs => {
      this.questions = qs.map((q: any) => {
        let opts = q.options;
        if (typeof opts === 'string') {
          opts = opts.includes('|') ? opts.split('|') : opts.split(',');
        } else if (Array.isArray(opts)) {
          if (opts.length === 1 && typeof opts[0] === 'string') {
            opts = opts[0].includes('|') ? opts[0].split('|') : opts[0].split(',');
          } else if (opts.length > 0 && typeof opts[0] === 'string' && opts[0].includes('|')) {
            opts = opts.join(',').split('|');
          }
        }
        
        // Remove 'A) ', 'b. ', etc. from the start of options since the HTML handles rendering letters
        if (Array.isArray(opts)) {
          opts = opts.map((opt: string) => opt.replace(/^[A-Da-d][\)\.]\s*/, '').trim());
        }

        return { ...q, options: opts };
      });
      this.loading = false;
      this.startTimer();
    });
  }

  startTimer(): void {
    this.timerSubscription = interval(1000).subscribe(() => {
      if (this.timeLeft > 0) {
        this.timeLeft--;
        this.calculateSpeedBonus();
      } else {
        this.submitQuiz();
      }
    });
  }

  calculateSpeedBonus(): void {
    const elapsed = 300 - this.timeLeft;
    if (elapsed < 120) this.speedBonus = 50; // Under 2 min = 50% bonus
    else if (elapsed < 180) this.speedBonus = 25; // Under 3 min = 25% bonus
    else if (elapsed < 240) this.speedBonus = 10; // Under 4 min = 10% bonus
    else this.speedBonus = 0;
  }

  get timeFormatted(): string {
    const min = Math.floor(this.timeLeft / 60);
    const sec = this.timeLeft % 60;
    return `${min}:${sec.toString().padStart(2, '0')}`;
  }

  get timeColor(): string {
    if (this.timeLeft > 180) return '#059669';
    if (this.timeLeft > 60) return '#D97706';
    return '#DC2626';
  }

  ngOnDestroy(): void {
    this.timerSubscription?.unsubscribe();
  }

  selectOption(questionId: number, optionIndex: number): void { this.selectedAnswers[questionId] = optionIndex; }
  isSelected(questionId: number, optionIndex: number): boolean { return this.selectedAnswers[questionId] === optionIndex; }
  get answeredCount(): number { return Object.keys(this.selectedAnswers).length; }
  get allAnswered(): boolean { return this.questions.every(q => this.selectedAnswers[q.questionId] !== undefined); }
  get progress(): number { return this.questions.length > 0 ? Math.round((this.answeredCount / this.questions.length) * 100) : 0; }

  submitQuiz(): void {
    this.timerSubscription?.unsubscribe();
    this.submitting = true;
    this.api.submitQuiz(this.auth.getUserId()!, { 
      quizId: this.quiz.quizId, 
      answers: this.selectedAnswers,
      speedBonus: this.speedBonus 
    }).subscribe({
      next: (result: any) => {
        result.speedBonus = this.speedBonus;
        this.router.navigate(['/quiz-result'], { state: { result } });
      },
      error: (err: any) => {
        console.error('Quiz submission error:', err);
        alert('Failed to submit quiz: ' + (err.error?.message || err.message || 'Unknown error. Check console and network tab.'));
        this.submitting = false; 
      }
    });
  }
}
