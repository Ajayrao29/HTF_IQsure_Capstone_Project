/*
 * ============================================================================
 * FILE: app.routes.ts | LOCATION: frontend/src/app/
 * PURPOSE: Defines ALL URL routes in the application.
 *          Maps each URL path to a page component.
 *          Also applies route guards to protect pages.
 *
 * ROUTE GUARDS:
 *   - AuthGuard (guards/auth.guard.ts) → Requires user to be logged in
 *   - AdminGuard (guards/admin.guard.ts) → Requires user to be an admin
 *
 * PUBLIC ROUTES (no guard):
 *   /              → LandingComponent (pages/landing/)
 *   /about         → AboutComponent (pages/about/)
 *   /login         → LoginComponent (pages/login/)
 *   /register      → RegisterComponent (pages/register/)
 *
 * USER ROUTES (AuthGuard — must be logged in):
 *   /dashboard     → DashboardComponent (pages/dashboard/)
 *   /quizzes       → QuizzesComponent (pages/quizzes/)
 *   /take-quiz/:id → TakeQuizComponent (pages/take-quiz/)
 *   /quiz-result   → QuizResultComponent (pages/quiz-result/)
 *   /policies      → PoliciesComponent (pages/policies/)
 *   /my-policies   → MyPoliciesComponent (pages/my-policies/)
 *   /badges        → BadgesComponent (pages/badges/)
 *   /rewards       → RewardsComponent (pages/rewards/)
 *   /leaderboard   → LeaderboardComponent (pages/leaderboard/)
 *   /achievements  → AchievementsComponent (pages/achievements/)
 *   /savings          → SavingsCalculatorComponent (pages/savings-calculator/)
 *   /risk-simulator   → RiskSimulatorComponent (pages/risk-simulator/)
 *
 * ADMIN ROUTES (AuthGuard + AdminGuard — must be logged in AND be admin):
 *   /admin/users          → AdminUsersComponent (pages/admin/users/)
 *   /admin/quiz-mgmt      → QuizMgmtComponent (pages/admin/quiz-mgmt/)
 *   /admin/policy-mgmt    → PolicyMgmtComponent (pages/admin/policy-mgmt/)
 *   /admin/badge-mgmt     → BadgeMgmtComponent (pages/admin/badge-mgmt/)
 *   /admin/reward-mgmt    → RewardMgmtComponent (pages/admin/reward-mgmt/)
 *   /admin/discount-rules → DiscountRulesComponent (pages/admin/discount-rules/)
 *
 * WILDCARD: /** → redirects unknown URLs to /login
 * ============================================================================
 */
import { Routes } from '@angular/router';
import { AuthGuard }  from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';
import { UnderwriterGuard } from './guards/underwriter.guard';
import { ClaimsOfficerGuard } from './guards/claims-officer.guard';

import { LandingComponent }       from './pages/landing/landing';
import { AboutComponent }         from './pages/about/about';
import { LoginComponent }         from './pages/login/login';
import { RegisterComponent }      from './pages/register/register';
import { DashboardComponent }     from './pages/dashboard/dashboard';
import { QuizzesComponent }       from './pages/quizzes/quizzes';
import { TakeQuizComponent }      from './pages/take-quiz/take-quiz';
import { QuizResultComponent }    from './pages/quiz-result/quiz-result';
import { PoliciesComponent }      from './pages/policies/policies';
import { MyPoliciesComponent }    from './pages/my-policies/my-policies';
import { BadgesComponent }        from './pages/badges/badges';
import { RewardsComponent }       from './pages/rewards/rewards';
import { LeaderboardComponent }   from './pages/leaderboard/leaderboard';
import { AchievementsComponent }  from './pages/achievements/achievements';
import { SavingsCalculatorComponent } from './pages/savings-calculator/savings-calculator';
import { RiskSimulatorComponent } from './pages/risk-simulator/risk-simulator';
import { EducationCenterComponent } from './pages/learning-center/education-center/education-center.component';
import { QuizReportsComponent } from './pages/learning-center/quiz-reports/quiz-reports.component';
import { ApplyPolicyComponent } from './pages/apply-policy/apply-policy';
import { MyClaimsComponent } from './pages/my-claims/my-claims';
import { FileClaimComponent } from './pages/file-claim/file-claim';
import { AdminUsersComponent }    from './pages/admin/users/users';
import { QuizMgmtComponent }      from './pages/admin/quiz-mgmt/quiz-mgmt';
import { PolicyMgmtComponent }    from './pages/admin/policy-mgmt/policy-mgmt';
import { BadgeMgmtComponent }     from './pages/admin/badge-mgmt/badge-mgmt';
import { RewardMgmtComponent }    from './pages/admin/reward-mgmt/reward-mgmt';
import { DiscountRulesComponent } from './pages/admin/discount-rules/discount-rules';
import { AdminDashboardComponent } from './pages/admin/dashboard/dashboard';
import { ManageCustomersComponent } from './pages/admin/customers/customers';
import { ManageUnderwritersComponent } from './pages/admin/underwriters/underwriters';
import { ManageClaimsOfficersComponent } from './pages/admin/claims-officers/claims-officers';
import { AssignUnderwriterComponent } from './pages/admin/assign-underwriter/assign-underwriter';
import { AssignClaimsOfficerComponent } from './pages/admin/assign-claims-officer/assign-claims-officer';

import { UnderwriterDashboardComponent } from './pages/underwriter/dashboard/dashboard';
import { UnderwriterPendingComponent } from './pages/underwriter/pending/pending';
import { UnderwriterPlansComponent } from './pages/underwriter/plans/plans';
import { UnderwriterMyPoliciesComponent } from './pages/underwriter/my-policies/my-policies';

import { ClaimsOfficerDashboardComponent } from './pages/claims-officer/dashboard/dashboard';
import { ClaimsOfficerClaimsComponent } from './pages/claims-officer/claims/claims';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'about', component: AboutComponent },
  { path: 'login',         component: LoginComponent },
  { path: 'register',      component: RegisterComponent },
  { path: 'dashboard',     component: DashboardComponent,   canActivate: [AuthGuard] },
  { path: 'quizzes',       component: QuizzesComponent,     canActivate: [AuthGuard] },
  { path: 'take-quiz/:id', component: TakeQuizComponent,    canActivate: [AuthGuard] },
  { path: 'quiz-result',   component: QuizResultComponent,  canActivate: [AuthGuard] },
  { path: 'policies',      component: PoliciesComponent,    canActivate: [AuthGuard] },
  { path: 'my-policies',   component: MyPoliciesComponent,  canActivate: [AuthGuard] },
  { path: 'badges',        component: BadgesComponent,      canActivate: [AuthGuard] },
  { path: 'rewards',       component: RewardsComponent,     canActivate: [AuthGuard] },
  { path: 'leaderboard',   component: LeaderboardComponent, canActivate: [AuthGuard] },
  { path: 'achievements',  component: AchievementsComponent, canActivate: [AuthGuard] },
  { path: 'savings',       component: SavingsCalculatorComponent, canActivate: [AuthGuard] },
  { path: 'risk-simulator', component: RiskSimulatorComponent, canActivate: [AuthGuard] },
  { path: 'learning-center/education-center', component: EducationCenterComponent, canActivate: [AuthGuard] },
  { path: 'learning-center/quiz-reports', component: QuizReportsComponent, canActivate: [AuthGuard] },
  { path: 'apply-policy', component: ApplyPolicyComponent, canActivate: [AuthGuard] },
  { path: 'my-claims',    component: MyClaimsComponent,    canActivate: [AuthGuard] },
  { path: 'file-claim',   component: FileClaimComponent,   canActivate: [AuthGuard] },
  { path: 'admin/users',          component: AdminUsersComponent,    canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/quiz-mgmt',      component: QuizMgmtComponent,      canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/policy-mgmt',    component: PolicyMgmtComponent,    canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/badge-mgmt',     component: BadgeMgmtComponent,     canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/reward-mgmt',    component: RewardMgmtComponent,    canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/discount-rules', component: DiscountRulesComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/dashboard',      component: AdminDashboardComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/customers',      component: ManageCustomersComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/underwriters',   component: ManageUnderwritersComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/claims-officers', component: ManageClaimsOfficersComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/assign-uw',      component: AssignUnderwriterComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/assign-officer', component: AssignClaimsOfficerComponent, canActivate: [AuthGuard, AdminGuard] },
  
  { path: 'underwriter/dashboard',   component: UnderwriterDashboardComponent, canActivate: [AuthGuard, UnderwriterGuard] },
  { path: 'underwriter/pending',     component: UnderwriterPendingComponent,   canActivate: [AuthGuard, UnderwriterGuard] },
  { path: 'underwriter/plans',       component: UnderwriterPlansComponent,     canActivate: [AuthGuard, UnderwriterGuard] },
  { path: 'underwriter/my-policies', component: UnderwriterMyPoliciesComponent, canActivate: [AuthGuard, UnderwriterGuard] },

  { path: 'claims-officer/dashboard', component: ClaimsOfficerDashboardComponent, canActivate: [AuthGuard, ClaimsOfficerGuard] },
  { path: 'claims-officer/claims',    component: ClaimsOfficerClaimsComponent,    canActivate: [AuthGuard, ClaimsOfficerGuard] },

  { path: '**', redirectTo: 'login' }
];

