/*
 * ============================================================================
 * FILE: models.ts | LOCATION: frontend/src/app/models/
 * PURPOSE: TypeScript interfaces that define the shape of data coming from the backend.
 *          These match the Java Response DTOs. When the backend sends JSON, Angular
 *          casts the data to these interfaces for type safety.
 *
 * EACH INTERFACE MAPS TO A BACKEND DTO:
 *   AuthResponse      → dto/auth/AuthResponse.java
 *   User              → dto/response/UserResponseDTO.java
 *   LeaderboardEntry  → dto/response/LeaderboardEntryDTO.java
 *   Quiz              → dto/response/QuizResponseDTO.java
 *   Question          → dto/response/QuestionResponseDTO.java
 *   AttemptResponse   → dto/response/AttemptResponseDTO.java
 *   Badge             → dto/response/BadgeResponseDTO.java
 *   Reward            → dto/response/RewardResponseDTO.java
 *   Policy            → dto/response/PolicyResponseDTO.java
 *   UserPolicy        → dto/response/UserPolicyResponseDTO.java
 *   PremiumBreakdown  → dto/response/PremiumBreakdownDTO.java
 *   AppliedDiscount   → dto/response/PremiumBreakdownDTO.AppliedDiscountDTO
 *   DiscountRule      → dto/response/DiscountRuleResponseDTO.java
 *
 * USED BY: All page components and services in the frontend
 * ============================================================================
 */

// Returned after login/register → stored in localStorage by AuthService
export interface AuthResponse { 
  token: string; tokenType: string; userId: number; name: string; email: string; role: string; 
  licenseNumber?: string;
  employeeId?: string;
  department?: string;
  approvalLimit?: number;
}

// User profile data → displayed on Dashboard, Admin Users page
export interface User { 
  userId: number; name: string; email: string; phone: string; userPoints: number; role: string; 
  licenseNumber?: string; specialization?: string; commissionPercentage?: number; totalQuotesSent?: number;
  employeeId?: string; department?: string; approvalLimit?: number; totalClaimsProcessed?: number;
  totalClaimsApproved?: number; totalClaimsRejected?: number;
  city?: string; state?: string; zipCode?: string; status?: string;
}

// Single row in the leaderboard → displayed on Leaderboard page
export interface LeaderboardEntry { rank: number; userId: number; name: string; userPoints: number; quizzesAttempted: number; }

// Quiz info → displayed on Quizzes page, Admin Quiz Management
export interface Quiz { quizId: number; title: string; category: string; difficulty: string; totalQuestions: number; }

// A single quiz question with options → displayed on Take Quiz page
export interface Question { questionId: number; quizId: number; text: string; options: string[]; explanation?: string; }

// Detailed question report for an attempt
export interface QuestionReport { questionText: string; selectedAnswer: string; correctAnswer: string; explanation: string; isCorrect: boolean; }

// Returned after submitting a quiz → displayed on Quiz Result page
export interface AttemptResponse { attemptId: number; userId: number; quizId: number; quizTitle: string; score: number; totalQuestions: number; percentage: number; pointsEarned: number; attemptDate: string; newBadgesUnlocked: Badge[]; questions?: QuestionReport[]; }

// Badge info → displayed on Badges page, Admin Badge Management
export interface Badge { badgeId: number; name: string; description: string; reqPoints: number; }

// Reward info → displayed on Rewards page, Admin Reward Management
export interface Reward { rewardId: number; rewardType: string; discountValue: number; expiryDate: string; }

// Insurance policy info → displayed on Policies page, Admin Policy Management
export interface Policy { 
  policyId: number; title: string; description: string; policyType: string; 
  basePremium: number; coverageAmount: number; durationMonths: number; 
  isActive: boolean;
  ageRange?: string;
  planType?: string;
  waitingPeriod?: string;
  hasMaternityCover?: boolean;
  hasPreExistingCover?: boolean;
}

// A user's purchased policy with discount details → displayed on My Policies page
export interface UserPolicy { 
  id: number; userId: number; userName: string; policyId: number; policyTitle: string; 
  policyType: string; basePremium: number; coverageAmount: number; durationMonths: number; 
  finalPremium: number; discountApplied: number; purchaseDate: string; status: string; 
  savedAmount: number; 
  assignedUnderwriterId?: number; assignedUnderwriterName?: string; assignedAt?: string;
  quoteAmount?: number; underwriterRemarks?: string; totalClaimedAmount?: number;
  remainingCoverage?: number;
  nomineeName?: string;
  nomineeRelationship?: string;
  healthReportPath?: string;
  insuredMembers?: InsuredMember[];
}

export interface InsuredMember {
  id?: number;
  fullName: string;
  relationship: string;
  dateOfBirth: string;
  gender: string;
  preExistingConditions: string;
}

// Detailed premium breakdown → displayed in the premium preview modal on Policies page
export interface PremiumBreakdown { policyId: number; policyTitle: string; policyType: string; basePremium: number; durationMonths: number; coverageAmount: number; userId: number; userPoints: number; badgesEarned: number; bestQuizScorePercent: number; appliedDiscounts: AppliedDiscount[]; totalDiscountPercent: number; discountedAmount: number; finalPremium: number; calculatedAt: string; }

// A single matched discount rule within a PremiumBreakdown
export interface AppliedDiscount { ruleName: string; discountPercentage: number; reason: string; }

// Discount rule configuration → displayed on Admin Discount Rules page
export interface DiscountRule { ruleId: number; ruleName: string; description: string; minQuizScorePercent: number; minUserPoints: number; minBadgesEarned: number; discountPercentage: number; applicablePolicyType: string | null; isActive: boolean; }

// Education content for learning center
export interface EducationContent { id: number; topic: string; language: string; title: string; content: string; }

// Insurance Claim info
export interface Claim {
  id: number;
  claimNumber: string;
  userId: number;
  userPolicyId: number;
  type: string;
  amount: number;
  approvedAmount?: number;
  settlementAmount?: number;
  hospitalName?: string;
  incidentDate?: string;
  diagnosis?: string;
  status: string;
  rejectionReason?: string;
  assignedOfficerId?: number;
  assignedOfficerName?: string;
  reviewStartedAt?: string;
  reviewedAt?: string;
  reviewerRemarks?: string;
  settlementDate?: string;
  createdAt: string;
}
