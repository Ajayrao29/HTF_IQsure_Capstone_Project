# 🛡️ IQsure - Gamified Insurance Literacy Platform

> **Learn Insurance. Earn Rewards. Save Money.**

IQsure is a modern, gamified insurance education platform built with Angular and Spring Boot. Users take interactive quizzes about insurance topics, earn points and badges, and unlock real premium discounts on insurance policies.

---

## ✨ Key Features

### 🎮 Advanced Gamification System
- **Interactive Quizzes** - Learn about Health, Life, and Motor insurance
- **Quiz Timer** - 5-minute countdown with speed bonuses (up to 50% extra points)
- **Points & Badges** - Earn 10 points per correct answer, unlock 4 progressive badges
- **Achievement System** - 8 unique achievements with bonus point rewards
- **Level System** - Progress through unlimited levels (100 points per level)
- **Day Streak** - Track daily engagement and maintain your streak
- **Leaderboard** - Compete with other users, top 3 get podium display
- **Social Sharing** - Share your achievements with friends
- **Reward System** - Redeem exclusive discount rewards

### 💰 Dynamic Premium Calculation & Savings
- **Smart Discounts** - Premium discounts automatically calculated based on:
  - Quiz performance (best score percentage)
  - Total points earned
  - Badges unlocked
  - Speed bonuses from quick completion
- **Transparent Pricing** - See full breakdown of applied discounts before purchase
- **Multiple Rules** - Stack multiple discount rules (up to 50% max discount)
- **Savings Calculator** - Track total savings and potential savings across all policies
- **Visual Comparisons** - Before/After pricing with clear savings display

### 🎨 Modern Design
- **The Hartford Branding** - Professional color scheme using Claret Red (#75013F) and Fuchsia Pink (#FE3082)
- **Responsive Layout** - Works seamlessly on desktop, tablet, and mobile
- **Intuitive UI** - Clean, modern interface that's easy to navigate
- **Smooth Animations** - Polished transitions and hover effects

### 🔐 Role-Based Access
- **User Role** - Take quizzes, browse policies, purchase insurance, redeem rewards
- **Admin Role** - Full CRUD operations on quizzes, policies, badges, rewards, and discount rules

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)

### Backend Setup
```bash
cd C:\Users\AJAY LINGAMPALLI\OneDrive\Documents\IQsure
mvn clean install
mvn spring-boot:run
```
Backend runs on: `http://localhost:8080`

### Frontend Setup
```bash
cd C:\Users\AJAY LINGAMPALLI\OneDrive\Documents\IQsure\frontend
npm install
ng serve
```
Frontend runs on: `http://localhost:4200`

### Default Credentials
**Admin:** `admin@iqsure.com` / `admin123`

⚠️ **Note:** Only admin user is pre-created. All other content (quizzes, policies, badges, rewards) must be created by admin through the UI. This is production-ready with no sample data.

---

## 🔒 Security Features

- **Password Hashing** - BCrypt encryption for all passwords
- **Input Validation** - Client and server-side validation
- **Email Uniqueness** - Prevents duplicate accounts
- **Admin Protection** - Admin users cannot be deleted
- **Role-Based Access** - Separate user and admin permissions
- **SQL Injection Prevention** - JPA parameterized queries
- **XSS Protection** - Angular built-in sanitization

---

## 📖 User Guide

### For Regular Users

#### 1. Register & Login
- Visit `http://localhost:4200`
- Create a new account or use demo credentials
- Login to access the dashboard

#### 2. Take Quizzes
- Navigate to **Quizzes** page
- Choose from 3 pre-loaded quizzes:
  - Health Insurance Basics (Easy)
  - Life Insurance Fundamentals (Medium)
  - Motor Insurance Knowledge (Hard)
- Answer all questions and submit
- Earn 10 points per correct answer (first attempt only)

#### 3. Unlock Badges
Badges unlock automatically as you accumulate points:
- **Quiz Beginner** - 10 points
- **Insurance Learner** - 50 points
- **Knowledge Pro** - 150 points
- **IQsure Champion** - 300 points

#### 4. Browse & Purchase Policies
- Go to **Policies** page
- Click **Preview My Premium** on any policy
- See your personalized discount breakdown
- Click **Buy This Policy** to purchase
- View purchased policies in **My Policies**

#### 5. Redeem Rewards
- Visit **Rewards** page
- Redeem available rewards before expiry
- Rewards can only be redeemed once

#### 6. Check Leaderboard
- See top users ranked by points
- Top 3 users displayed on podium
- Track your ranking

### For Admin Users

#### 1. Manage Users
- **Admin → Users**
- View all registered users
- Delete users (except admins)

#### 2. Manage Quizzes
- **Admin → Quizzes**
- Create new quizzes with title, category, difficulty
- Add questions with 4 multiple-choice options
- Set correct answers
- Edit or delete existing quizzes

#### 3. Manage Policies
- **Admin → Policies**
- Create insurance policies with:
  - Title, description, type (Health/Life/Motor)
  - Base premium, coverage amount, duration
  - Active/inactive status
- Edit or delete policies

#### 4. Manage Badges
- **Admin → Badges**
- Create achievement badges
- Set point requirements
- Delete badges

#### 5. Manage Rewards
- **Admin → Rewards**
- Create discount rewards
- Set discount value and expiry date
- Delete rewards

#### 6. Manage Discount Rules
- **Admin → Discount Rules**
- Create dynamic discount rules with conditions:
  - Minimum quiz score percentage
  - Minimum user points
  - Minimum badges earned
- Set discount percentage
- Apply to specific policy types or all
- Toggle active/inactive status

---

## 🎯 How Discounts Work

### Discount Rule Engine

The platform uses a sophisticated rule engine to calculate premium discounts:

1. **User Engagement Data**
   - Total points earned
   - Number of badges unlocked
   - Best quiz score percentage

2. **Rule Evaluation**
   - Each active discount rule is checked
   - ALL conditions in a rule must be met
   - Rules can be policy-type specific or apply to all

3. **Discount Calculation**
   - Matching rules' discounts are summed
   - Maximum 50% discount cap applied
   - Final premium = Base premium × (1 - Total discount %)

### Pre-seeded Discount Rules

1. **Quiz Scorer** (5% off)
   - Condition: Score ≥ 60% on any quiz
   - Applies to: All policies

2. **Points Achiever** (10% off)
   - Conditions: 100+ points AND 1+ badge
   - Applies to: All policies

3. **Health Expert Bonus** (15% off)
   - Conditions: Score ≥ 80% AND 50+ points AND 2+ badges
   - Applies to: Health policies only

**Example:** A user with 150 points, 2 badges, and 85% best score purchasing a health policy would get:
- Quiz Scorer: 5%
- Points Achiever: 10%
- Health Expert Bonus: 15%
- **Total: 30% discount!**

---

## 🏗️ Technical Architecture

### Backend (Spring Boot)
```
src/main/java/org/hartford/iqsure/
├── config/          # CORS, Data Seeding, Security
├── controller/      # REST API endpoints
├── dto/            # Request/Response DTOs
├── entity/         # JPA entities
├── exception/      # Error handling
├── repository/     # Database access
└── service/        # Business logic
```

**Key Technologies:**
- Spring Boot 3.x
- Spring Data JPA
- H2 In-Memory Database
- Lombok
- Swagger/OpenAPI

### Frontend (Angular)
```
frontend/src/app/
├── components/     # Reusable components (navbar)
├── guards/         # Route protection (auth, admin)
├── models/         # TypeScript interfaces
├── pages/          # Page components
└── services/       # API & Auth services
```

**Key Technologies:**
- Angular 17+
- TypeScript
- SCSS
- RxJS
- HttpClient

### Database Schema

**Core Entities:**
- `users` - User accounts with points and role
- `quizzes` - Quiz templates
- `questions` - Quiz questions with options
- `answers` - Correct answers
- `quiz_attempts` - User quiz submissions
- `badges` - Achievement badges
- `user_badges` - Badges earned by users
- `policies` - Insurance policy templates
- `user_policies` - Purchased policies
- `discount_rules` - Dynamic discount rules
- `premium_calculation_logs` - Audit trail
- `rewards` - Redeemable rewards
- `user_rewards` - Redeemed rewards

---

## 🎨 Design System

### Color Palette
```scss
--claret:     #75013F  // Primary brand color
--fuchsia:    #FE3082  // Accent & CTAs
--warm-gray:  #EAE5DF  // Text & backgrounds
--white:      #FFFFFF  // Clean backgrounds
--light-bg:   #F8F6F4  // Page background
```

### Typography
- **Font Family:** Inter (Google Fonts)
- **Weights:** 400, 500, 600, 700, 800
- **Base Size:** 16px

### Components
- **Buttons:** Primary (fuchsia), Secondary (outlined), Danger (red)
- **Cards:** White background with subtle shadows
- **Forms:** Clean inputs with focus states
- **Tables:** Striped rows with hover effects
- **Modals:** Centered overlays with backdrop blur

---

## 📊 Sample Data

The application auto-seeds with sample data on startup:

### Quizzes (3)
1. Health Insurance Basics - 4 questions
2. Life Insurance Fundamentals - 5 questions
3. Motor Insurance Knowledge - 5 questions

### Policies (6)
1. Basic Health Shield - $300/yr
2. Premium Health Guard - $750/yr
3. Term Life Protector - $500/yr
4. Whole Life Legacy Plan - $1200/yr
5. Basic Auto Cover - $200/yr
6. Comprehensive Auto Shield - $650/yr

### Badges (4)
- Quiz Beginner (10 pts)
- Insurance Learner (50 pts)
- Knowledge Pro (150 pts)
- IQsure Champion (300 pts)

### Rewards (3)
- Premium Discount - 5% off
- Health Bonus - 10% off
- Loyalty Reward - 15% off

---

## 🔧 API Documentation

### Swagger UI
Access interactive API docs at: `http://localhost:8080/swagger-ui.html`

### Key Endpoints

**Auth:**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get token

**Quizzes:**
- `GET /api/v1/quizzes` - List all quizzes
- `GET /api/v1/quizzes/{id}` - Get quiz details
- `POST /api/v1/quizzes` - Create quiz (admin)

**Attempts:**
- `POST /api/v1/attempts?userId={id}` - Submit quiz
- `GET /api/v1/attempts?userId={id}` - Get user attempts

**Policies:**
- `GET /api/v1/policies` - List active policies
- `GET /api/v1/users/{userId}/premium/calculate/{policyId}` - Calculate premium
- `POST /api/v1/users/{userId}/policies` - Purchase policy

**Badges:**
- `GET /api/v1/badges` - List all badges
- `GET /api/v1/badges/user/{userId}` - Get user badges

**Rewards:**
- `GET /api/v1/rewards` - List all rewards
- `POST /api/v1/rewards/{id}/redeem?userId={id}` - Redeem reward

**Discount Rules:**
- `GET /api/v1/discount-rules` - List active rules
- `POST /api/v1/discount-rules` - Create rule (admin)

---

## 🛠️ Development

### Running Tests
```bash
# Backend
mvn test

# Frontend
ng test
```

### Building for Production
```bash
# Backend
mvn clean package
java -jar target/IQsure-0.0.1-SNAPSHOT.jar

# Frontend
ng build --configuration production
# Deploy dist/ folder to web server
```

### Database Console
Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:iqsuredb`
- Username: `sa`
- Password: `password123`

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Windows - Kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Run frontend on different port
ng serve --port 4201
```

### CORS Errors
- Ensure backend is running on port 8080
- Check `WebConfig.java` has correct origins

### Database Not Seeding
- Delete H2 database file and restart
- Check console logs for errors

### Module Not Found (Frontend)
```bash
rm -rf node_modules package-lock.json
npm install
```

---

## 📝 License

This project is created for educational purposes as part of The Hartford Insurance gamification initiative.

---

## 👥 Contributors

Built with ❤️ by the IQsure Team

---

## 📞 Support

For questions or issues:
1. Check the [END_TO_END_SETUP_GUIDE.md](END_TO_END_SETUP_GUIDE.md)
2. Review API docs at `/swagger-ui.html`
3. Check console logs for errors

---

**Happy Learning! 🚀**
