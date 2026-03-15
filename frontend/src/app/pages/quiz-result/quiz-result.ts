/*
 * FILE: quiz-result.ts | LOCATION: pages/quiz-result/
 * PURPOSE: Quiz result page (URL: /quiz-result). Shows score, percentage, points earned.
 *          Data is passed via router state from TakeQuizComponent after submission.
 * TEMPLATE: quiz-result.html | STYLES: quiz-result.scss
 * DATA FROM: TakeQuizComponent → router.navigate(['/quiz-result'], { state: { result } })
 */
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';


@Component({
  selector: 'app-quiz-result',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './quiz-result.html',
  styleUrls: ['./quiz-result.scss']
})
export class QuizResultComponent implements OnInit {
  result: any = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.result = history.state?.result;
    if (!this.result) this.router.navigate(['/quizzes']);
  }

  get scoreColor(): string {
    return this.result?.percentage >= 80 ? '#22c55e' : this.result?.percentage >= 50 ? '#eab308' : '#ef4444';
  }

  get scoreMessage(): string {
    return this.result?.percentage >= 80 ? '🎉 Excellent work!' : this.result?.percentage >= 50 ? '👍 Good effort!' : '📚 Keep practicing!';
  }
}
