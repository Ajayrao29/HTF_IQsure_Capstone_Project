# IQsure Backend API Documentation

## Overview
The IQsure backend is a robust RESTful API built with **Spring Boot**. It handles the core business logic for gamified insurance literacy, including user management, insurance policy lifecycle, gamification rules, and secure claim processing.

## Technology Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Persistence**: Spring Data JPA
- **Database**: H2 (Embedded / File-based for portability)
- **Security**: Spring Security with JWT (JSON Web Token)
- **Documentation**: Swagger / OpenAPI 3.0
- **Build Tool**: Maven

---

## Architecture & Design Patterns
- **Controller-Service-Repository**: Standard layered architecture for separation of concerns.
- **DTO (Data Transfer Objects)**: Ensures clean API contracts and prevents sensitive entity exposure.
- **Global Exception Handling**: Centralized management of application errors using `@ControllerAdvice`.
- **JWT Authentication**: Stateless authentication mechanism where every request is verified via a token in the header.

---

## Core Modules & Functionalities

### 1. Authentication & User Management
- **Security Context**: Uses Spring Security to secure endpoints.
- **Role Hierarchy**: Supports `CUSTOMER`, `ADMIN`, `UNDERWRITER`, and `CLAIMS_OFFICER`.
- **Leveling Logic**: Business logic that calculates user levels and points based on quiz performance.

### 2. Insurance Engine
- **Policy Lifecycle**: Manages stages from `PENDING_REVIEW` to `ACTIVE` and `EXPIRED`.
- **Flexible Policies**: Supports various coverage types (Life, Health, Motor, Travel).
- **Premium Calculator**: Dynamic calculation engine that applies discounts based on gamification rewards and quiz scores.

### 3. Gamification System
- **Quiz Processing**: Validates user answers and calculates score bonuses for speed.
- **Badge Engine**: Automatically awards badges to users when specific milestones are met.
- **Reward System**: Tracks points spendable on insurance premium discounts.
- **Rule Engine**: Allows admins to define and update the logic for premium discounts (e.g., "5% off for Level 5").

### 4. Claim & Pipeline Management
- **Claim Workflow**: Tracks claims from filing to officer assignment, verification, and settlement.
- **Task Pipeline**: Admin controller for orchestrating work between underwriters and claims officers.

---

## API Endpoints Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/register` | Register a new user. |
| POST | `/login` | Authenticate and receive a JWT token. |

### Users (`/api/v1/users`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | `/{id}` | Get detailed user profile. |
| GET | `/leaderboard` | Get list of top users by points. |
| GET | `/role/{role}` | Filter users by their role. |
| PUT | `/{id}/status` | Update user account status (active/inactive). |

### Quizzes & Education (`/api/v1/quizzes`, `/api/v1/questions`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | `/quizzes` | List all available quizzes. |
| GET | `/questions/quiz/{id}` | Get all questions for a specific quiz. |
| POST | `/attempts` | Submit quiz answers and calculate score. |
| GET | `/education` | Fetch insurance educational articles. |

### Insurance Policies (`/api/v1/policies`, `/api/v1/users/{id}/policies`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | `/policies` | Get list of active insurance plans. |
| POST | `/{userId}/policies` | Purchase/apply for a policy. |
| GET | `/{userId}/premium/calculate/{policyId}` | Calculate discounted premium before purchase. |
| PUT | `/{userId}/policies/{userPolicyId}/pay` | Complete payment/activation. |

### Claims (`/api/v1/claims`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/file` | Submit a new insurance claim. |
| GET | `/user/{userId}` | Get all claims for a specific user. |
| PUT | `/{id}/process` | (Officer) Approve or reject a claim. |
| PUT | `/{id}/settle` | (Officer) Mark a claim as settled with final amount. |

### Admin & Pipeline (`/api/v1/admin/pipeline`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | `/policies` | Master list of all user policy applications. |
| PUT | `/policies/{id}/assign` | Assign a policy to an Underwriter. |
| PUT | `/policies/{id}/quote` | (Underwriter) Submit a premium quote for an application. |

---

## Data Model (Entities)
- **User**: Name, Email, Role, Points, Level.
- **Policy**: Name, Description, Base Premium, Duration.
- **UserPolicy**: Intersection between User and Policy with status (Draft, Pending, Active).
- **Quiz**: Topic, Difficulty, Points possible.
- **Badge**: Metadata for achievements.
- **Claim**: Description, Documentation, Amount requested, Status.
- **DiscountRule**: Logic for discount percentages based on user stats.
