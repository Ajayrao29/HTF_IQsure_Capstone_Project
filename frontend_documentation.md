# IQsure Frontend Documentation

## Overview
The IQsure frontend is a modern, responsive web application built using **Angular** and **Ionic Framework**. It provides a gamified experience for insurance literacy, allowing users to learn about insurance through quizzes, earn rewards, and manage their insurance policies and claims in a seamless, interactive interface.

## Technology Stack
- **Framework**: Angular (Latest)
- **UI Library**: Ionic Framework (for mobile-friendly components and layout)
- **Styling**: SCSS (Global and Component-specific)
- **State Management**: RxJS Observables with Angular Services
- **Authentication**: JWT (JSON Web Token) with Auth Interceptor
- **Icons**: Ionicons

---

## Core Functionalities

### 1. User Authentication & Authorization
- **Registration**: Allows new users to create accounts.
- **Login**: Secure login using JWT.
- **Role-Based Access Control**: Different views and permissions for **Customer**, **Underwriter**, **Claims Officer**, and **Admin**.
- **Route Guards**: Protects sensitive routes based on authentication status and user roles.

### 2. Gamified Insurance Learning (Customer)
- **Quizzes**: Browse and take interactive quizzes on various insurance topics.
- **Quiz Scoring**: Score points based on correct answers and speed.
- **Badges & Leveling**: Earn badges for milestones (e.g., "First Quiz", "Perfect Score") and level up as points accumulate.
- **Leaderboard**: Compete with other users for the top spot on the global leaderboard.
- **Learning Center**: Access educational articles and videos about insurance concepts.

### 3. Policy Management (Customer)
- **Policy Discovery**: Browse available insurance policies (Life, Health, Motor, etc.).
- **Dynamic Premium Calculation**: See how quiz points and earned rewards affect premium discounts in real-time.
- **Apply Policy**: Submit applications for new policies, including family member details and document uploads.
- **My Policies**: View active, pending, and expired policies with status tracking.

### 4. Claim Management (Customer)
- **File a Claim**: intuitive multi-step process to file claims for active policies.
- **Claim Tracking**: Real-time status updates on submitted claims.

### 5. Underwriter Tools
- **Review Applications**: Dedicated dashboard to review pending policy applications.
- **Quote Risk**: Tools to assess risk and quote final premiums for applicants.

### 6. Claims Officer Tools
- **Claim Processing**: Review evidence, approve or reject claims, and manage settlement amounts.
- **Dashboard**: Track workload and pending claim assignments.

### 7. Administration
- **User Management**: Manage accounts for all roles.
- **Content Management**: CRUD operations for Quizzes, Questions, Policies, and Educational Content.
- **Rule Engine**: Manage dynamic discount rules and gamification parameters.
- **Pipeline Monitoring**: Assign tasks to Underwriters and Claims Officers.

---

## Component Documentation

### Main Layout Components
| Component | Location | Description |
| :--- | :--- | :--- |
| `AppComponent` | `src/app/app.ts` | The root component holding the main navigation and router outlet. |
| `NavbarComponent` | `src/app/components/navbar/` | Dynamic navigation bar that changes based on user role and auth status. |

### Page Components (Public)
| Component | Route | Description |
| :--- | :--- | :--- |
| `LandingComponent` | `/` | Marketing landing page introducing IQsure. |
| `AboutComponent` | `/about` | Information about the team and mission. |
| `LoginComponent` | `/login` | Secure entry point for users. |
| `RegisterComponent` | `/register` | User onboarding and account creation. |

### Page Components (Customer)
| Component | Route | Description |
| :--- | :--- | :--- |
| `DashboardComponent` | `/dashboard` | Main hub showing user stats, levels, and quick actions. |
| `QuizzesComponent` | `/quizzes` | Grid view of available insurance quizzes. |
| `TakeQuizComponent` | `/take-quiz/:id` | Interactive quiz interface with timer and progress tracking. |
| `QuizResultComponent` | `/quiz-result` | Summary board after a quiz, showing points earned and level progress. |
| `PoliciesComponent` | `/policies` | Marketplace for browsing available insurance products. |
| `ApplyPolicyComponent` | `/apply-policy` | Form-heavy component for policy application. |
| `MyPoliciesComponent` | `/my-policies` | User's personal vault of purchased policies. |
| `MyClaimsComponent` | `/my-claims` | List and status tracking of personal insurance claims. |
| `LeaderboardComponent` | `/leaderboard` | Top players ranking table. |
| `RewardsComponent` | `/rewards` | Catalog of redeemable rewards and active discounts. |

### Page Components (Admin)
| Component | Route | Description |
| :--- | :--- | :--- |
| `AdminDashboardComponent` | `/admin/dashboard` | High-level system overview for administrators. |
| `QuizMgmtComponent` | `/admin/quiz-mgmt` | Management interface for creating and editing quizzes. |
| `PolicyMgmtComponent` | `/admin/policy-mgmt` | Tools to design and update insurance policy products. |
| `AdminUsersComponent` | `/admin/users` | Master list of all system users with role management. |
| `AssignUnderwriterComponent`| `/admin/assign-uw` | Workflow to assign new policy applications to underwriters. |

---

## Data Models (Interfaces)

The frontend uses TypeScript interfaces to ensure type safety when handling data from the backend. These models are defined in `src/app/models/models.ts`.

| Model | Purpose |
| :--- | :--- |
| `User` | Represents the user profile, including points, level, and role-specific fields. |
| `Quiz` | Metadata for an insurance literacy quiz. |
| `Question` | A single quiz question with multiple-choice options. |
| `AttemptResponse` | The result returned after submitting a quiz, including points earned and badges unlocked. |
| `Policy` | Details of an insurance product available for purchase. |
| `UserPolicy` | A policy purchased by a user, including status, premium paid, and discounts applied. |
| `Claim` | Details of an insurance claim, including status and settlement info. |
| `PremiumBreakdown`| Detailed calculation showing how the final premium was derived from the base price. |
| `Badge` | Achievement markers earned by users. |
| `DiscountRule` | Configuration data for the dynamic discount engine. |

---

## State Management & Communication

### API Integration
All communication with the Spring Boot backend is abstracted through the `ApiService`. This service handles:
- **GET** requests for fetching lists and profiles.
- **POST** requests for submitting forms and quiz attempts.
- **PUT** requests for updating statuses and processing claims.
- **DELETE** requests for removal of records by admins.

### Authentication Flow
1. User provides credentials in `LoginComponent`.
2. `AuthService` calls the backend and receives a JWT token.
3. Token is stored in browser `localStorage`.
4. `AuthInterceptor` captures all subsequent HTTP calls and attaches the `Authorization: Bearer <token>` header.
5. `AuthGuard` checks for a valid token before allowing access to protected routes.
