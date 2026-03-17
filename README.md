# 🛡️ IQsure - Comprehensive Gamified Insurance Platform

> **Learn Insurance. Earn Rewards. Secure Your Future.**

IQsure is a state-of-the-art, gamified insurance education and management platform built with **Angular 17** and **Spring Boot 3**. It bridges the gap between insurance literacy and actual policy management, allowing users to learn through interactive quizzes while providing a full lifecycle for insurance policies—from application and underwriting to claims settlement.

---

## ✨ Key Features

### 🏢 Multi-Role Insurance Ecosystem
- **Regular User** - Interactive learning, policy application, rewards redemption, and claims filing.
- **Underwriter Portal** - Evaluate risk, review medical reports, and send personalized premium quotes.
- **Claims Officer Portal** - Verify claim documents, process settlements, and manage approval limits.
- **Admin Dashboard** - Total control over quizzes, rewards, discount rules, and system users.

### 🎮 Advanced Gamification & Literacy
- **Interactive Quizzes** - Level up your insurance knowledge across Health, Life, and Motor categories.
- **Multilingual Learning** - Education content available in **English, Spanish, Hindi, Telugu, and Kannada**.
- **Dynamic Leaderboard** - Compete with others and earn your spot on the podium.
- **Badges & Achievements** - Unlock status symbols like "Quick Learner," "Insurance Pro," and "Claim Hero."
- **Day Streaks** - Maintain your learning momentum with daily engagement tracking.

### 💰 Smart Discount Engine
- **Performance-Based Pricing** - Premium discounts automatically calculated based on quiz scores and points.
- **Rule-Based Automation** - Admin-defined rules that stack (up to 50% max discount).
- **Transparency** - Clear breakdown of savings and applied discounts before purchase.

### 📄 Full Policy Lifecycle
- **Detailed Application** - Add family members, specify pre-existing conditions, and upload documents.
- **Risk Assessment** - Underwriters review applications and documents before approval.
- **Digital Claims** - File claims with document evidence and track settlement status in real-time.
- **Notifications** - Stay updated on policy status, quotes, and claim decisions.

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (OpenJDK recommended)
- **Node.js 18+**
- **Angular CLI** (`npm install -g @angular/cli`)
- **Maven** 3.8+

### Backend Setup
```bash
# Navigate to project root
mvn clean install
mvn spring-boot:run
```
Backend runs on: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Frontend Setup
```bash
cd frontend
npm install
ng serve
```
Frontend runs on: `http://localhost:4200`

### Default Credentials (Auto-seeded)
| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@iqsure.com` | `admin123` |
| **User** | `user@iqsure.com` | `user123` |
| **Underwriter** | `underwriter@iqsure.com` | `underwriter123` |
| **Claims Officer** | `bob@iqsure.com` | `claims123` |

---

## 🏗️ Technical Architecture

### Tech Stack
- **Frontend**: Angular 17, TypeScript, SCSS, RxJS, Bootstrap/Vanilla CSS.
- **Backend**: Spring Boot 3.4.x, Spring Data JPA, Spring Security (JWT), Spring Validation.
- **Database**: H2 In-Memory Database (Production-ready schema).
- **Documentation**: SpringDoc OpenAPI (Swagger).

### Backend Project Structure
```text
src/main/java/org/hartford/iqsure/
├── config/          # Security, CORS, Data Seeding
├── controller/      # REST API Endpoints (Admin, Claims, Underwriting, etc.)
├── dto/            # Data Transfer Objects for validation
├── entity/         # JPA Entities (User, Policy, Claim, Quiz, etc.)
├── repository/     # Data Access Layer
└── service/        # Core Business Logic & Rule Engine
```

### Frontend Project Structure
```text
frontend/src/app/
├── components/     # UI components (Navbar, User Dashboard)
├── guards/         # Auth & Role-based route protection
├── pages/          # Feature-specific pages (Admin, Underwriter, Claims)
├── services/       # API integration services
└── models/         # TypeScript interfaces
```

---

## 📖 Feature Walkthrough

### 1. Learning & Gamification
Users start by exploring the **Learning Center** in their preferred language. They take quizzes to earn **Points** and **Badges**. These points directly impact the "Smart Discount" they receive when applying for policies.

### 2. Policy Application
Users browse available policies and "Apply." During application, they can:
- Upload required documents (Medical reports, ID proof).
- Add family members for "Family Floater" plans.
- Declare pre-existing conditions.

### 3. Underwriting Workflow
A policy application moves to **PENDING_UNDERWRITING**. 
- **Assign**: Admins/Systems assign it to an Underwriter.
- **Review**: Underwriters review the risk profile and documents.
- **Quote**: Underwriters send a final quote which the user can accept to activate the policy.

### 4. Claims Management
Once a policy is **ACTIVE**, users can "File a Claim."
- **Submit**: Upload hospital bills and discharge summaries.
- **Process**: Claims Officers receive the claim and verify it against policy terms.
- **Settle**: If within their `approvalLimit`, officers can approve and settle the claim.

---

## 📊 Sample Data Seeding
The application automatically seeds the following on startup:
- **3 Categories of Quizzes** (Health, Life, Motor) with 5 questions each.
- **6 Premium Health Plans** with varying coverage and eligibility.
- **4 Achievement Badges** and **3 Reward Types**.
- **5 Dynamic Discount Rules** (e.g., "Health Policy Expert" - 20% off).
- **Initial Test Accounts** for all roles.

---

## 🔧 API Documentation
Access the interactive Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

### Key Functional Endpoints
- `POST /api/auth/login` - Authenticate and receive JWT.
- `GET /api/v1/users/{userId}/dashboard/stats` - Fetch personalized gamification/policy stats.
- `POST /api/v1/users/{userId}/apply-policy` - Submit a new policy application.
- `GET /api/v1/underwriter/assignments` - List pending applications for underwriters.
- `POST /api/v1/claims` - File a new claim.
- `GET /api/v1/claims-officer/pending` - List claims awaiting verification.

---

## 🛠️ Development & Testing

### Running Tests
```bash
# Backend
mvn test

# Frontend
ng test
```

### Database Console
Access H2 console at: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:iqsuredb`
- **User**: `sa` | **Password**: `password123`

---

## 📝 License
This project is an internal development initiative for **The Hartford Insurance**. All rights reserved.

---

Built with ❤️ by the **IQsure Dev Team**
