import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  AuthResponse, User, LeaderboardEntry, Quiz, Question, 
  AttemptResponse, Badge, Reward, Policy, UserPolicy, 
  PremiumBreakdown, DiscountRule, EducationContent, Claim 
} from '../models/models';

/**
 * Central service for backend API communication.
 * Best Practice: Use TypeScript interfaces for type safety and avoid 'any'.
 */
const API = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class ApiService {

  constructor(private http: HttpClient) {}

  // ─── Authentication ───────────────────────────────────────────────────

  /** Register a new user account */
  register(data: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/api/auth/register`, data);
  }

  /** Authenticate user and get session token */
  login(data: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/api/auth/login`, data);
  }

  /** Request password reset OTP */
  forgotPassword(email: string): Observable<void> {
    return this.http.post<void>(`${API}/api/auth/forgot-password`, { email });
  }

  /** Reset password using OTP */
  resetPassword(data: { otp: string, newPassword: any }): Observable<void> {
    return this.http.post<void>(`${API}/api/auth/reset-password`, data);
  }

  // ─── User Management ──────────────────────────────────────────────────

  /** Get detailed user profile */
  getProfile(userId: number): Observable<User> {
    return this.http.get<User>(`${API}/api/v1/users/${userId}`);
  }

  /** Update user profile information */
  updateProfile(userId: number, data: any): Observable<User> {
    return this.http.put<User>(`${API}/api/v1/users/${userId}`, data);
  }

  /** Get list of all users (Admin only) */
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${API}/api/v1/users`);
  }

  /** Delete a user account */
  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/users/${userId}`);
  }

  /** Get top users ranked by points */
  getLeaderboard(): Observable<LeaderboardEntry[]> {
    return this.http.get<LeaderboardEntry[]>(`${API}/api/v1/users/leaderboard`);
  }

  /** Get users filtered by their role */
  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${API}/api/v1/users/role/${role}`);
  }

  /** Update user's active/inactive status */
  updateUserStatus(userId: number, status: string): Observable<User> {
    return this.http.put<User>(`${API}/api/v1/users/${userId}/status?status=${status}`, {});
  }

  /** Create a new underwriter account */
  createUnderwriter(data: any): Observable<User> {
    return this.http.post<User>(`${API}/api/v1/users/underwriter`, data);
  }

  /** Create a new claims officer account */
  createClaimsOfficer(data: any): Observable<User> {
    return this.http.post<User>(`${API}/api/v1/users/claims-officer`, data);
  }

  // ─── Claims Management ────────────────────────────────────────────────

  /** Get all claims (Basic summary) */
  getAllClaims(): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${API}/api/v1/claims`);
  }

  /** Get all claims with full details (Admin) */
  getAllClaimsAdmin(): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${API}/api/v1/claims/all`);
  }

  /** Get claims submitted by a specific user */
  getClaimsByUser(userId: number): Observable<Claim[]> {
    return this.http.get<Claim[]>(`${API}/api/v1/claims/user/${userId}`);
  }

  /** Get claim details by ID */
  getClaimById(claimId: number): Observable<Claim> {
    return this.http.get<Claim>(`${API}/api/v1/claims/${claimId}`);
  }

  /** Submit a new insurance claim */
  fileClaim(userId: number, userPolicyId: number, data: any): Observable<Claim> {
    return this.http.post<Claim>(`${API}/api/v1/claims/file?userId=${userId}&userPolicyId=${userPolicyId}`, data);
  }

  /** Assign a claims officer to a claim */
  assignClaimOfficer(claimId: number, officerId: number): Observable<Claim> {
    return this.http.put<Claim>(`${API}/api/v1/claims/${claimId}/assign?officerId=${officerId}`, {});
  }

  /** Process a claim (Approve/Reject) */
  processClaim(claimId: number, status: string, remarks: string, approvedAmount?: number): Observable<Claim> {
    const amountParam = approvedAmount !== undefined ? `&approvedAmount=${approvedAmount}` : '';
    return this.http.put<Claim>(`${API}/api/v1/claims/${claimId}/process?status=${status}&remarks=${encodeURIComponent(remarks)}${amountParam}`, {});
  }

  /** Record claim settlement */
  settleClaim(claimId: number, settlementAmount: number): Observable<Claim> {
    return this.http.put<Claim>(`${API}/api/v1/claims/${claimId}/settle?settlementAmount=${settlementAmount}`, {});
  }

  // ─── Admin Pipeline ───────────────────────────────────────────────────

  /** Get all user policies across the system for admin pipeline */
  getAllUserPoliciesAdmin(): Observable<UserPolicy[]> {
    return this.http.get<UserPolicy[]>(`${API}/api/v1/admin/pipeline/policies`);
  }

  /** Get user policies filtered by status */
  getPoliciesByStatus(status: string): Observable<UserPolicy[]> {
    return this.http.get<UserPolicy[]>(`${API}/api/v1/admin/pipeline/policies/status?status=${status}`);
  }

  /** Assign an underwriter to a policy application */
  assignUnderwriter(userPolicyId: number, underwriterId: number): Observable<UserPolicy> {
    return this.http.put<UserPolicy>(`${API}/api/v1/admin/pipeline/policies/${userPolicyId}/assign?underwriterId=${underwriterId}`, {});
  }

  /** Send a premium quote to the user */
  sendQuote(userPolicyId: number, quoteAmount: number, remarks: string): Observable<UserPolicy> {
    return this.http.put<UserPolicy>(`${API}/api/v1/admin/pipeline/policies/${userPolicyId}/quote`, { quoteAmount, remarks });
  }

  /** Activate a policy after payment/review */
  activatePolicyByAdmin(userPolicyId: number): Observable<UserPolicy> {
    return this.http.put<UserPolicy>(`${API}/api/v1/admin/pipeline/policies/${userPolicyId}/activate`, {});
  }

  /** Reject a policy application */
  rejectPolicy(userPolicyId: number, remarks: string): Observable<UserPolicy> {
    return this.http.put<UserPolicy>(`${API}/api/v1/admin/pipeline/policies/${userPolicyId}/reject`, { remarks });
  }

  /** Get performance stats for an underwriter */
  getUnderwriterStats(underwriterId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/admin/pipeline/underwriter/stats?underwriterId=${underwriterId}`);
  }

  /** Get stats for a claims officer */
  getClaimsOfficerStats(officerId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/admin/pipeline/officer/stats?officerId=${officerId}`);
  }

  /** Trigger AI analysis for a policy application */
  getAiAnalysis(policyId: number): Observable<any> {
    return this.http.get<any>(`${API}/api/v1/admin/pipeline/policies/${policyId}/ai-analysis`);
  }

  // ─── Gamification (Quizzes, Badges, Rewards) ───────────────────────────

  /** Get all available educational quizzes */
  getAllQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${API}/api/v1/quizzes`);
  }

  /** Get quiz details by ID */
  getQuizById(quizId: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${API}/api/v1/quizzes/${quizId}`);
  }

  /** Get questions associated with a quiz */
  getQuestionsByQuiz(quizId: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${API}/api/v1/questions/quiz/${quizId}`);
  }

  /** Submit quiz attempt for scoring */
  submitQuiz(userId: number, data: { quizId: number; answers: { [questionId: number]: number }, speedBonus?: number }): Observable<AttemptResponse> {
    return this.http.post<AttemptResponse>(`${API}/api/v1/attempts?userId=${userId}`, data);
  }

  /** Get attempt history for a user */
  getAttemptsByUser(userId: number): Observable<AttemptResponse[]> {
    return this.http.get<AttemptResponse[]>(`${API}/api/v1/attempts?userId=${userId}`);
  }

  /** Get all system badges */
  getAllBadges(): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${API}/api/v1/badges`);
  }

  /** Get badges earned by a user */
  getBadgesByUser(userId: number): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${API}/api/v1/badges/user/${userId}`);
  }

  /** Get all reward types */
  getAllRewards(): Observable<Reward[]> {
    return this.http.get<Reward[]>(`${API}/api/v1/rewards`);
  }

  /** Get rewards earned by a user */
  getEarnedRewardsByUser(userId: number): Observable<Reward[]> {
    return this.http.get<Reward[]>(`${API}/api/v1/rewards/user/${userId}/earned`);
  }

  /** Redeem points for a reward */
  redeemReward(rewardId: number, userId: number): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/rewards/${rewardId}/redeem?userId=${userId}`, {});
  }

  // ─── Policy Catalog & Purchase ────────────────────────────────────────

  /** Get list of active insurance policies for customers */
  getActivePolicies(): Observable<Policy[]> {
    return this.http.get<Policy[]>(`${API}/api/v1/policies`);
  }

  /** Calculate personalized premium based on quiz performance and rewards */
  calculatePremium(userId: number, policyId: number, selectedRewardIds: number[] = []): Observable<PremiumBreakdown> {
    const params = selectedRewardIds.length > 0 ? '?selectedRewardIds=' + selectedRewardIds.join('&selectedRewardIds=') : '';
    return this.http.get<PremiumBreakdown>(`${API}/api/v1/users/${userId}/premium/calculate/${policyId}${params}`);
  }

  /** Purchase an insurance policy */
  purchasePolicy(userId: number, data: { policyId: number, rewardIds?: number[] }, selectedRewardIds: number[] = []): Observable<UserPolicy> {
    const params = selectedRewardIds.length > 0 ? '?selectedRewardIds=' + selectedRewardIds.join('&selectedRewardIds=') : '';
    return this.http.post<UserPolicy>(`${API}/api/v1/users/${userId}/policies${params}`, data);
  }

  /** Get all policies purchased by a user */
  getUserPolicies(userId: number): Observable<UserPolicy[]> {
    return this.http.get<UserPolicy[]>(`${API}/api/v1/users/${userId}/policies`);
  }

  /** Get rewards available for a user to apply to a policy */
  getAvailableRewardsForUser(userId: number): Observable<Reward[]> {
    return this.http.get<Reward[]>(`${API}/api/v1/users/${userId}/premium/available-rewards`);
  }

  // ─── Policy Management (Admin) ───────────────────────────────────────

  /** Get all policies in the catalog (Admin only) */
  getAllPolicies(): Observable<Policy[]> {
    return this.http.get<Policy[]>(`${API}/api/v1/policies/all`);
  }

  /** Create a new policy template */
  createPolicy(data: Partial<Policy>): Observable<Policy> {
    return this.http.post<Policy>(`${API}/api/v1/policies`, data);
  }

  /** Update an existing policy template */
  updatePolicy(policyId: number, data: Partial<Policy>): Observable<Policy> {
    return this.http.put<Policy>(`${API}/api/v1/policies/${policyId}`, data);
  }

  /** Permanent deletion of a policy */
  deletePolicy(policyId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/policies/${policyId}`);
  }

  // ─── Discount Rules Management ───────────────────────────────────────

  /** Get all discount rules (Admin) */
  getAllDiscountRules(): Observable<DiscountRule[]> {
    return this.http.get<DiscountRule[]>(`${API}/api/v1/discount-rules/all`);
  }

  /** Create a new discount rule */
  createDiscountRule(data: any): Observable<DiscountRule> {
    return this.http.post<DiscountRule>(`${API}/api/v1/discount-rules`, data);
  }

  /** Update a discount rule */
  updateDiscountRule(ruleId: number, data: any): Observable<DiscountRule> {
    return this.http.put<DiscountRule>(`${API}/api/v1/discount-rules/${ruleId}`, data);
  }

  /** Delete a discount rule */
  deleteDiscountRule(ruleId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/discount-rules/${ruleId}`);
  }

  // ─── Gamification Management (Admin) ──────────────────────────────────

  /** Create a new badge */
  createBadge(data: any): Observable<Badge> {
    return this.http.post<Badge>(`${API}/api/v1/badges`, data);
  }

  /** Update an existing badge */
  updateBadge(badgeId: number, data: any): Observable<Badge> {
    return this.http.put<Badge>(`${API}/api/v1/badges/${badgeId}`, data);
  }

  /** Permanent deletion of a badge */
  deleteBadge(badgeId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/badges/${badgeId}`);
  }

  /** Create a new quiz */
  createQuiz(data: any): Observable<Quiz> {
    return this.http.post<Quiz>(`${API}/api/v1/quizzes`, data);
  }

  /** Update an existing quiz */
  updateQuiz(quizId: number, data: any): Observable<Quiz> {
    return this.http.put<Quiz>(`${API}/api/v1/quizzes/${quizId}`, data);
  }

  /** Permanent deletion of a quiz */
  deleteQuiz(quizId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/quizzes/${quizId}`);
  }

  /** Add a new question to a quiz */
  addQuestion(data: any): Observable<Question> {
    return this.http.post<Question>(`${API}/api/v1/questions`, data);
  }

  /** Add an answer option to a question */
  addAnswer(data: any): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/questions/answers`, data);
  }

  /** Permanent deletion of a question */
  deleteQuestion(questionId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/questions/${questionId}`);
  }

  /** Create a new reward */
  createReward(data: any): Observable<Reward> {
    return this.http.post<Reward>(`${API}/api/v1/rewards`, data);
  }

  /** Permanent deletion of a reward */
  deleteReward(rewardId: number): Observable<void> {
    return this.http.delete<void>(`${API}/api/v1/rewards/${rewardId}`);
  }

  // ─── Education & Utilities ───────────────────────────────────────────

  /** Get educational content by language */
  getEducationContentByLanguage(language: string): Observable<EducationContent[]> {
    return this.http.get<EducationContent[]>(`${API}/api/v1/education?language=${language}`);
  }

  /** Get notifications for a user */
  getNotifications(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${API}/api/v1/notifications/user/${userId}`);
  }

  /** Mark a specific notification as read */
  markNotificationAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${API}/api/v1/notifications/${notificationId}/read`, {});
  }

  /** Mark all unread notifications for a user as read */
  markAllNotificationsAsRead(userId: number): Observable<void> {
    return this.http.put<void>(`${API}/api/v1/notifications/user/${userId}/read-all`, {});
  }

  /** Upload a file for claim documentation */
  uploadFile(file: File): Observable<{ filePath: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ filePath: string }>(`${API}/api/v1/files/upload`, formData);
  }

  /** Get TTS audio blob for a text chunk */
  getTtsAudio(text: string, lang: string): Observable<Blob> {
    return this.http.get(`https://translate.googleapis.com/translate_tts?ie=UTF-8&q=${encodeURIComponent(text)}&tl=${lang}&client=gtx`, { responseType: 'blob' });
  }

  /** Pay for a policy */
  payPolicy(userId: number, userPolicyId: number): Observable<any> {
    return this.http.post<any>(`${API}/api/v1/users/${userId}/policies/${userPolicyId}/pay`, {});
  }

  /** Get policies assigned to an underwriter by status */
  getUnderwriterPoliciesByStatus(underwriterId: number, status?: string): Observable<any[]> {
    const statusParam = status ? `?status=${status}` : '';
    return this.http.get<any[]>(`${API}/api/v1/admin/pipeline/underwriter/${underwriterId}/policies${statusParam}`);
  }
}


