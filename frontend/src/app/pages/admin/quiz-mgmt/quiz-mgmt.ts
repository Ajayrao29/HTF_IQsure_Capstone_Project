/*
 * FILE: quiz-mgmt.ts | LOCATION: pages/admin/quiz-mgmt/
 * PURPOSE: Admin Quiz Management page (URL: /admin/quiz-mgmt). Admin can:
 *          - Create, edit, delete quizzes
 *          - Add questions to quizzes (with comma-separated options)
 *          - Set correct answers for questions
 *          - Delete questions
 * TEMPLATE: quiz-mgmt.html | STYLES: quiz-mgmt.scss
 * CALLS: api.service.ts → getAllQuizzes(), createQuiz(), updateQuiz(), deleteQuiz(),
 *        getQuestionsByQuiz(), addQuestion(), addAnswer(), deleteQuestion()
 * BACKEND: QuizController, QuestionController
 * GUARD: AdminGuard — only accessible by admin users
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-quiz-mgmt', standalone: true, imports: [CommonModule, FormsModule], templateUrl: './quiz-mgmt.html', styleUrls: ['./quiz-mgmt.scss'] })
export class QuizMgmtComponent implements OnInit {
  quizzes: any[] = []; loading = true; showForm = false; editingQuiz: any = null;
  form: any = { title: '', category: '', difficulty: 'EASY' };
  selectedQuiz: any = null; questions: any[] = [];
  questionForm: any = { text: '', options: '', explanation: '' }; showQuestionForm = false;
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadQuizzes(); }
  loadQuizzes(): void { this.api.getAllQuizzes().subscribe(q => { this.quizzes = q; this.loading = false; }); }
  openCreate(): void { this.editingQuiz = null; this.form = { title: '', category: '', difficulty: 'EASY' }; this.showForm = true; }
  openEdit(q: any): void { this.editingQuiz = q; this.form = { title: q.title, category: q.category, difficulty: q.difficulty }; this.showForm = true; }
  saveQuiz(): void {
    const obs = this.editingQuiz ? this.api.updateQuiz(this.editingQuiz.quizId, this.form) : this.api.createQuiz(this.form);
    obs.subscribe(() => { this.showForm = false; this.loadQuizzes(); });
  }
  deleteQuiz(id: number): void { if (!confirm('Delete this quiz?')) return; this.api.deleteQuiz(id).subscribe(() => this.loadQuizzes()); }
  manageQuestions(quiz: any): void {
    this.selectedQuiz = quiz;
    this.api.getQuestionsByQuiz(quiz.quizId).subscribe(qs => {
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
        if (Array.isArray(opts)) {
          opts = opts.map((opt: string) => opt.replace(/^[A-Da-d][\)\.]\s*/, '').trim());
        }
        return { ...q, options: opts };
      });
    });
  }
  addQuestion(): void {
    this.api.addQuestion({ quizId: this.selectedQuiz.quizId, text: this.questionForm.text, options: this.questionForm.options, explanation: this.questionForm.explanation }).subscribe((q: any) => {
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
      if (Array.isArray(opts)) {
        opts = opts.map((opt: string) => opt.replace(/^[A-Da-d][\)\.]\s*/, '').trim());
      }
      this.questions.push({ ...q, options: opts, explanation: this.questionForm.explanation });
      this.questionForm = { text: '', options: '', explanation: '' };
      this.showQuestionForm = false;
    });
  }
  setAnswer(questionId: number): void {
    const text = prompt('Correct answer text:'); const idx = Number(prompt('Correct option index (0=A,1=B,2=C,3=D):'));
    if (text === null) return; this.api.addAnswer({ questionId, text, rightOption: idx }).subscribe();
  }
  deleteQuestion(id: number): void { if (!confirm('Delete question?')) return; this.api.deleteQuestion(id).subscribe(() => this.questions = this.questions.filter(q => q.questionId !== id)); }
}
