/*
 * ============================================================================
 * FILE: api.service.ts | LOCATION: frontend/src/app/services/
 * PURPOSE: Central service that handles ALL HTTP calls to the Spring Boot backend.
 *          Every page component uses this service to talk to the backend APIs.
 *          Base URL: http://localhost:8080
 *
 * HOW IT WORKS:
 *   - Each method sends an HTTP request (GET/POST/PUT/DELETE) to a backend endpoint
 *   - Returns an Observable (RxJS) — the component must .subscribe() to get the result
 *   - The authInterceptor (auth.interceptor.ts) automatically adds the Bearer token
 *
 * MAPS TO BACKEND CONTROLLERS:
 *   Auth     → AuthController.java (POST /api/auth/...)
 *   Users    → UserController.java (GET/DELETE /api/v1/users/...)
 *   Quizzes  → QuizController.java (GET/POST/PUT/DELETE /api/v1/quizzes/...)
 *   Questions→ QuestionController.java (/api/v1/questions/...)
 *   Attempts → AttemptController.java (/api/v1/attempts/...)
 *   Badges   → BadgeController.java (/api/v1/badges/...)
 *   Rewards  → RewardController.java (/api/v1/rewards/...)
 *   Policies → PolicyController.java (/api/v1/policies/...)
 *   Premium  → UserPolicyController.java (/api/v1/users/{id}/premium/...)
 *   User Policies → UserPolicyController.java (/api/v1/users/{id}/policies/...)
 *   Discount Rules → DiscountRuleController.java (/api/v1/discount-rules/...)
 * ============================================================================
 */
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Backend base URL — Spring Boot runs on port 8080
const API = 'http://localhost:8080';

@Injectable({ providedIn: 'root' }) // Available everywhere in the app (singleton)
export class ApiService {

  constructor(private http: HttpClient) {} // Angular's HttpClient for making HTTP requests

  // ─── Auth ─────────────────────────────────────────────────────────────
  // → AuthController.java → POST /api/auth/register
  register(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/auth/register`, data);
  }

  // → AuthController.java → POST /api/auth/login
  login(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/auth/login`, data);
  }

  // ─── Users ────────────────────────────────────────────────────────────
  // → UserController.java → GET /api/v1/users/{userId}
  getProfile(userId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/users/${userId}`);
  }

  // → UserController.java → GET /api/v1/users
  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users`);
  }

  // → UserController.java → DELETE /api/v1/users/{userId}
  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/users/${userId}`);
  }

  // → UserController.java → GET /api/v1/users/leaderboard
  getLeaderboard(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users/leaderboard`);
  }

  getUsersByRole(role: string): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users/role/${role}`);
  }

  updateUserStatus(userId: number, status: string): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/users/${userId}/status?status=${status}`, {});
  }

  createUnderwriter(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/users/underwriter`, data);
  }

  createClaimsOfficer(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/users/claims-officer`, data);
  }

  // ─── Claims ───────────────────────────────────────────────────────────
  getAllClaims(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/claims`);
  }

  getAllClaimsAdmin(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/claims/all`);
  }

  getClaimsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/claims/user/${userId}`);
  }

  getClaimById(claimId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/claims/${claimId}`);
  }

  fileClaim(userId: number, userPolicyId: number, data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/claims/file?userId=${userId}&userPolicyId=${userPolicyId}`, data);
  }

  assignClaimOfficer(claimId: number, officerId: number): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/claims/${claimId}/assign?officerId=${officerId}`, {});
  }

  processClaim(claimId: number, status: string, remarks: string, approvedAmount?: number): Observable<any> {
    const amountParam = approvedAmount !== undefined ? `&approvedAmount=${approvedAmount}` : '';
    return this.http.put<any>(`${API}/api/v1/claims/${claimId}/process?status=${status}&remarks=${remarks}${amountParam}`, {});
  }

  // ─── Pipeline (Admin) ─────────────────────────────────────────────────
  getAllUserPoliciesAdmin(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/admin/pipeline/policies`);
  }

  getPoliciesByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/admin/pipeline/policies/status?status=${status}`);
  }

  assignUnderwriter(userPolicyId: number, underwriterId: number): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/admin/pipeline/policies/${userPolicyId}/assign?underwriterId=${underwriterId}`, {});
  }

  sendQuote(userPolicyId: number, quoteAmount: number, remarks: string): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/admin/pipeline/policies/${userPolicyId}/quote?quoteAmount=${quoteAmount}&remarks=${remarks}`, {});
  }

  activatePolicyByAdmin(userPolicyId: number): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/admin/pipeline/policies/${userPolicyId}/activate`, {});
  }

  getUnderwriterStats(): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/admin/pipeline/underwriter/stats`);
  }

  getClaimsOfficerStats(): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/admin/pipeline/officer/stats`);
  }

  // ─── Quizzes ──────────────────────────────────────────────────────────
  // → QuizController.java → GET /api/v1/quizzes
  getAllQuizzes(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/quizzes`);
  }

  // → QuizController.java → GET /api/v1/quizzes/{quizId}
  getQuizById(quizId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/quizzes/${quizId}`);
  }

  // → QuizController.java → POST /api/v1/quizzes
  createQuiz(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/quizzes`, data);
  }

  // → QuizController.java → PUT /api/v1/quizzes/{quizId}
  updateQuiz(quizId: number, data: any): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/quizzes/${quizId}`, data);
  }

  // → QuizController.java → DELETE /api/v1/quizzes/{quizId}
  deleteQuiz(quizId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/quizzes/${quizId}`);
  }

  // ─── Questions ────────────────────────────────────────────────────────
  // → QuestionController.java → GET /api/v1/questions/quiz/{quizId}
  getQuestionsByQuiz(quizId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/questions/quiz/${quizId}`);
  }

  // → QuestionController.java → POST /api/v1/questions
  addQuestion(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/questions`, data);
  }

  // → QuestionController.java → POST /api/v1/questions/answers
  addAnswer(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/questions/answers`, data);
  }

  // → QuestionController.java → DELETE /api/v1/questions/{questionId}
  deleteQuestion(questionId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/questions/${questionId}`);
  }

  // ─── Attempts ─────────────────────────────────────────────────────────
  // → AttemptController.java → POST /api/v1/attempts?userId=X
  // Sends: { quizId, answers: { questionId: selectedOptionIndex, ... } }
  submitQuiz(userId: number, data: { quizId: number; answers: { [questionId: number]: number }, speedBonus?: number }): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/attempts?userId=${userId}`, data);
  }

  // → AttemptController.java → GET /api/v1/attempts?userId=X
  getAttemptsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/attempts?userId=${userId}`);
  }

  // ─── Badges ───────────────────────────────────────────────────────────
  // → BadgeController.java → GET /api/v1/badges
  getAllBadges(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/badges`);
  }

  // → BadgeController.java → GET /api/v1/badges/user/{userId}
  getBadgesByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/badges/user/${userId}`);
  }

  // → BadgeController.java → POST /api/v1/badges
  createBadge(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/badges`, data);
  }

  // → BadgeController.java → DELETE /api/v1/badges/{badgeId}
  deleteBadge(badgeId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/badges/${badgeId}`);
  }

  // → BadgeController.java → PUT /api/v1/badges/{badgeId}
  updateBadge(badgeId: number, data: any): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/badges/${badgeId}`, data);
  }

  // ─── Rewards ──────────────────────────────────────────────────────────
  // → RewardController.java → GET /api/v1/rewards
  getAllRewards(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/rewards`);
  }

  // → RewardController.java → GET /api/v1/rewards/user/{userId}
  getRewardsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/rewards/user/${userId}`);
  }

  // → RewardController.java → GET /api/v1/rewards/user/{userId}/earned
  getEarnedRewardsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/rewards/user/${userId}/earned`);
  }

  // → RewardController.java → POST /api/v1/rewards/{rewardId}/redeem?userId=X
  redeemReward(rewardId: number, userId: number): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/rewards/${rewardId}/redeem?userId=${userId}`, {});
  }

  // → RewardController.java → POST /api/v1/rewards
  createReward(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/rewards`, data);
  }

  // → RewardController.java → DELETE /api/v1/rewards/{rewardId}
  deleteReward(rewardId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/rewards/${rewardId}`);
  }

  // ─── Policies ─────────────────────────────────────────────────────────
  // → PolicyController.java → GET /api/v1/policies (active only, for users)
  getActivePolicies(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/policies`);
  }

  // → PolicyController.java → GET /api/v1/policies/all (all including inactive, for admin)
  getAllPolicies(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/policies/all`);
  }

  // → PolicyController.java → POST /api/v1/policies
  createPolicy(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/policies`, data);
  }

  // → PolicyController.java → PUT /api/v1/policies/{policyId}
  updatePolicy(policyId: number, data: any): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/policies/${policyId}`, data);
  }

  // → PolicyController.java → DELETE /api/v1/policies/{policyId}
  deletePolicy(policyId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/policies/${policyId}`);
  }

  // ─── User Policies & Premium ──────────────────────────────────────────
  // → UserPolicyController.java → GET /api/v1/users/{userId}/premium/calculate/{policyId}
  calculatePremium(userId: number, policyId: number, selectedRewardIds: number[] = []): Observable<any> {
    const params = selectedRewardIds.length > 0 ? '?selectedRewardIds=' + selectedRewardIds.join('&selectedRewardIds=') : '';
    return this.http.get<any>(`${API}/api/v1/users/${userId}/premium/calculate/${policyId}${params}`);
  }

  // → UserPolicyController.java → POST /api/v1/users/{userId}/policies
  purchasePolicy(userId: number, data: { policyId: number, rewardIds?: number[] }, selectedRewardIds: number[] = []): Observable<any> {
    const params = selectedRewardIds.length > 0 ? '?selectedRewardIds=' + selectedRewardIds.join('&selectedRewardIds=') : '';
    return this.http.post<any>(`${API}/api/v1/users/${userId}/policies${params}`, data);
  }

  // → UserPolicyController.java → GET /api/v1/users/{userId}/premium/available-rewards
  getAvailableRewardsForUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users/${userId}/premium/available-rewards`);
  }

  // → UserPolicyController.java → GET /api/v1/users/{userId}/policies
  getUserPolicies(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/users/${userId}/policies`);
  }

  // ─── Discount Rules ───────────────────────────────────────────────────
  // → DiscountRuleController.java → GET /api/v1/discount-rules/all
  getAllDiscountRules(): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/discount-rules/all`);
  }

  // → DiscountRuleController.java → POST /api/v1/discount-rules
  createDiscountRule(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/discount-rules`, data);
  }

  // → DiscountRuleController.java → PUT /api/v1/discount-rules/{ruleId}
  updateDiscountRule(ruleId: number, data: any): Observable<any> {
    return this.http.put<any>(`${API}/api/v1/discount-rules/${ruleId}`, data);
  }

  // → DiscountRuleController.java → DELETE /api/v1/discount-rules/{ruleId}
  deleteDiscountRule(ruleId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/discount-rules/${ruleId}`);
  }

  // ─── Education Content ────────────────────────────────────────────────
  // → EducationContentController.java → GET /api/v1/education?language=lang
  getEducationContentByLanguage(language: string): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/education?language=${language}`);
  }

  // → EducationContentController.java → GET /api/v1/education/tts
  getTtsAudio(text: string, language: string): Observable<Blob> {
    return this.http.get(`${API}/api/v1/education/tts?language=${language}&text=${encodeURIComponent(text)}`, { responseType: 'blob' });
  }
}

