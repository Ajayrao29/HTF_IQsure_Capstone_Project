import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { Quiz, Question } from '../../../models/models';

/**
 * Admin component for building and managing educational quizzes.
 * Best Practice: Explicit typing and modular method structure.
 */
@Component({
  selector: 'app-quiz-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-mgmt.html',
  styleUrls: ['./quiz-mgmt.scss']
})
export class QuizMgmtComponent implements OnInit {
  quizzes: Quiz[] = [];
  loading = true;
  showForm = false;
  editingQuiz: Quiz | null = null;
  
  form: Partial<Quiz> = { title: '', category: '', difficulty: 'EASY' };
  
  selectedQuiz: Quiz | null = null;
  questions: Question[] = [];
  questionForm = { text: '', options: '', explanation: '' };
  showQuestionForm = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  /** Reload all quizzes from the API */
  loadQuizzes(): void {
    this.loading = true;
    this.api.getAllQuizzes().subscribe({
      next: (data) => {
        this.quizzes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load quizzes:', err);
        this.loading = false;
      }
    });
  }

  /** Trigger modal for new quiz Creation */
  openCreate(): void {
    this.editingQuiz = null;
    this.form = { title: '', category: '', difficulty: 'EASY' };
    this.showForm = true;
  }

  /** Trigger modal for editing quiz metadata */
  openEdit(quiz: Quiz): void {
    this.editingQuiz = quiz;
    this.form = { ...quiz };
    this.showForm = true;
  }

  /** Save or Update quiz metadata */
  saveQuiz(): void {
    this.loading = true;
    const request = this.editingQuiz 
      ? this.api.updateQuiz(this.editingQuiz.quizId, this.form) 
      : this.api.createQuiz(this.form);

    request.subscribe({
      next: () => {
        this.showForm = false;
        this.loadQuizzes();
      },
      error: (err) => {
        console.error('Failed to save quiz:', err);
        this.loading = false;
        alert('Could not save quiz. Check console for details.');
      }
    });
  }

  /** Permanent deletion of a quiz and its questions */
  deleteQuiz(id: number): void {
    if (!confirm('Are you sure you want to delete this quiz? All questions will be lost.')) {
      return;
    }

    this.api.deleteQuiz(id).subscribe({
      next: () => this.loadQuizzes(),
      error: (err) => console.error('Failed to delete quiz:', err)
    });
  }

  /** Loads questions for a specific quiz to be managed */
  manageQuestions(quiz: Quiz): void {
    this.selectedQuiz = quiz;
    this.api.getQuestionsByQuiz(quiz.quizId).subscribe({
      next: (qs) => {
        this.questions = qs.map(q => ({
          ...q,
          options: this.parseOptions(q.options)
        }));
      },
      error: (err) => console.error('Failed to load questions:', err)
    });
  }

  /** Adds a new question with options (comma or pipe separated) */
  addQuestion(): void {
    if (!this.selectedQuiz) return;

    const data = {
      quizId: this.selectedQuiz.quizId,
      text: this.questionForm.text,
      options: this.questionForm.options,
      explanation: this.questionForm.explanation
    };

    this.api.addQuestion(data).subscribe({
      next: (q) => {
        this.questions.push({
          ...q,
          options: this.parseOptions(q.options)
        });
        this.questionForm = { text: '', options: '', explanation: '' };
        this.showQuestionForm = false;
      },
      error: (err) => console.error('Failed to add question:', err)
    });
  }

  /** Sets the correct answer index for a question */
  setAnswer(questionId: number): void {
    const text = prompt('Enter the exact correct answer text:');
    const idx = Number(prompt('Enter correct option index (0=A, 1=B, 2=C, 3=D):'));

    if (text === null || isNaN(idx)) return;

    this.api.addAnswer({ questionId, text, rightOption: idx }).subscribe({
      next: () => alert('Answer set successfully.'),
      error: (err) => console.error('Failed to set answer:', err)
    });
  }

  /** Permanent deletion of a question */
  deleteQuestion(id: number): void {
    if (!confirm('Are you sure you want to delete this question?')) {
      return;
    }

    this.api.deleteQuestion(id).subscribe({
      next: () => this.questions = this.questions.filter(q => q.questionId !== id),
      error: (err) => console.error('Failed to delete question:', err)
    });
  }

  /** Helper to normalize quiz option strings/arrays */
  private parseOptions(opts: any): string[] {
    if (!opts) return [];
    
    let result: string[] = [];
    if (typeof opts === 'string') {
      result = opts.includes('|') ? opts.split('|') : opts.split(',');
    } else if (Array.isArray(opts)) {
      if (opts.length === 1 && typeof opts[0] === 'string') {
        result = opts[0].includes('|') ? opts[0].split('|') : opts[0].split(',');
      } else {
        result = opts;
      }
    }

    return result.map(opt => opt.replace(/^[A-Da-d][\)\.]\s*/, '').trim());
  }
}
