# IQsure - Complete End-to-End Setup Guide

## 🎯 What is IQsure?
IQsure is a gamified insurance literacy platform where users take quizzes about insurance, earn points and badges, and unlock real premium discounts on policies.

---

## 📋 Prerequisites

### Required Software:
1. **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
2. **Node.js 18+** - [Download](https://nodejs.org/)
3. **Angular CLI** - Install via: `npm install -g @angular/cli`
4. **Maven** (comes with most Java installations)
5. **Any IDE** - IntelliJ IDEA, VS Code, or Eclipse

---

## 🚀 Backend Setup (Spring Boot)

### Step 1: Navigate to Backend
```bash
cd C:\Users\AJAY LINGAMPALLI\OneDrive\Documents\IQsure
```

### Step 2: Build the Project
```bash
mvn clean install
```

### Step 3: Run the Backend
```bash
mvn spring-boot:run
```

**Backend will start on:** `http://localhost:8080`

### Step 4: Verify Backend
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:iqsuredb`
  - Username: `sa`
  - Password: `password123`

---

## 🎨 Frontend Setup (Angular)

### Step 1: Navigate to Frontend
```bash
cd C:\Users\AJAY LINGAMPALLI\OneDrive\Documents\IQsure\frontend
```

### Step 2: Install Dependencies
```bash
npm install
```

### Step 3: Run the Frontend
```bash
ng serve
```

**Frontend will start on:** `http://localhost:4200`

---

## 👤 Default Login Credentials

### Admin Account:
- **Email:** `admin@iqsure.com`
- **Password:** `admin123`

### Demo User Account:
- **Email:** `user@iqsure.com`
- **Password:** `user123`

---

## 🎮 How to Use the Application

### For Regular Users:

1. **Register/Login**
   - Go to `http://localhost:4200`
   - Register a new account or login with demo credentials

2. **Take Quizzes**
   - Navigate to "Quizzes" page
   - Select a quiz (Health, Life, or Motor Insurance)
   - Answer all questions
   - Submit to earn points!

3. **Earn Badges**
   - Badges unlock automatically as you earn points:
     - 10 pts → Quiz Beginner
     - 50 pts → Insurance Learner
     - 150 pts → Knowledge Pro
     - 300 pts → IQsure Champion

4. **Browse Policies**
   - Go to "Policies" page
   - Click "Preview My Premium" on any policy
   - See your personalized discount based on quiz performance!

5. **Purchase Policies**
   - After previewing, click "Buy This Policy"
   - View purchased policies in "My Policies"

6. **Redeem Rewards**
   - Visit "Rewards" page
   - Redeem available rewards before they expire

7. **Check Leaderboard**
   - See how you rank against other users
   - Top 3 users get special podium display!

### For Admin Users:

1. **Login as Admin**
   - Use admin credentials above

2. **Manage Users**
   - Admin → Users
   - View all registered users
   - Delete users (except admins)

3. **Manage Quizzes**
   - Admin → Quizzes
   - Create new quizzes
   - Add questions with multiple choice options
   - Set correct answers

4. **Manage Policies**
   - Admin → Policies
   - Create/Edit/Delete insurance policies
   - Set base premiums and coverage amounts
   - Toggle active/inactive status

5. **Manage Badges**
   - Admin → Badges
   - Create achievement badges
   - Set point requirements

6. **Manage Rewards**
   - Admin → Rewards
   - Create discount rewards
   - Set expiry dates

7. **Manage Discount Rules**
   - Admin → Discount Rules
   - Create dynamic discount rules
   - Set conditions (min score, points, badges)
   - Apply to specific policy types or all

---

## 🎯 Key Features

### Gamification System:
- ✅ Quiz-based learning
- ✅ Points system (10 pts per correct answer)
- ✅ Progressive badge unlocking
- ✅ Leaderboard rankings
- ✅ Reward redemption

### Dynamic Premium Calculation:
- ✅ Base premium from policy
- ✅ Automatic discount application based on:
  - Quiz scores (best score %)
  - Total points earned
  - Badges unlocked
- ✅ Multiple discount rules can stack
- ✅ Maximum 50% discount cap
- ✅ Full calculation breakdown shown

### Sample Discount Rules (Pre-seeded):
1. **Quiz Scorer** - 5% off for 60%+ quiz score
2. **Points Achiever** - 10% off for 100+ points & 1+ badge
3. **Health Expert Bonus** - 15% off health policies for 80%+ score & 2+ badges

---

## 📊 Sample Data (Auto-seeded)

### Quizzes:
1. Health Insurance Basics (Easy) - 4 questions
2. Life Insurance Fundamentals (Medium) - 5 questions
3. Motor Insurance Knowledge (Hard) - 5 questions

### Policies:
1. Basic Health Shield - $300/yr
2. Premium Health Guard - $750/yr
3. Term Life Protector - $500/yr
4. Whole Life Legacy Plan - $1200/yr
5. Basic Auto Cover - $200/yr
6. Comprehensive Auto Shield - $650/yr

### Badges:
1. Quiz Beginner (10 pts)
2. Insurance Learner (50 pts)
3. Knowledge Pro (150 pts)
4. IQsure Champion (300 pts)

### Rewards:
1. Premium Discount - 5% off
2. Health Bonus - 10% off
3. Loyalty Reward - 15% off

---

## 🛠️ Troubleshooting

### Backend Issues:

**Port 8080 already in use:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080
# Kill the process (replace PID)
taskkill /PID <PID> /F
```

**Database not seeding:**
- Delete the H2 database file and restart
- Check console logs for errors

### Frontend Issues:

**Port 4200 already in use:**
```bash
ng serve --port 4201
```

**CORS errors:**
- Ensure backend is running on port 8080
- Check WebConfig.java has correct origins

**Module not found:**
```bash
rm -rf node_modules package-lock.json
npm install
```

---

## 🎨 Design System

### The Hartford Brand Colors:
- **Claret Red:** `#75013F` - Primary brand color
- **Fuchsia Pink:** `#FE3082` - Accent & CTAs
- **Warm Gray:** `#EAE5DF` - Text & backgrounds
- **White:** `#FFFFFF` - Clean backgrounds
- **Black:** `#000000` - Used sparingly

### Typography:
- **Font:** Inter (Google Fonts)
- **Weights:** 400, 500, 600, 700, 800

---

## 📁 Project Structure

```
IQsure/
├── src/main/java/org/hartford/iqsure/
│   ├── config/          # CORS, Data Seeding
│   ├── controller/      # REST API endpoints
│   ├── dto/            # Request/Response objects
│   ├── entity/         # Database models
│   ├── exception/      # Error handling
│   ├── repository/     # Database access
│   └── service/        # Business logic
├── frontend/src/app/
│   ├── components/     # Reusable components (navbar)
│   ├── guards/         # Route protection
│   ├── models/         # TypeScript interfaces
│   ├── pages/          # All page components
│   └── services/       # API & Auth services
└── END_TO_END_SETUP_GUIDE.md
```

---

## 🔄 Development Workflow

### Making Changes:

1. **Backend Changes:**
   - Edit Java files
   - Maven auto-recompiles (if using IDE)
   - Or restart: `mvn spring-boot:run`

2. **Frontend Changes:**
   - Edit TypeScript/HTML/SCSS files
   - Angular auto-reloads in browser
   - Check console for errors

### Testing Flow:

1. Start backend first
2. Start frontend second
3. Open browser to `http://localhost:4200`
4. Register new user
5. Take a quiz
6. Check points increased
7. Browse policies
8. See personalized discounts!

---

## 🚢 Deployment (Optional)

### Backend (Spring Boot):
```bash
mvn clean package
java -jar target/IQsure-0.0.1-SNAPSHOT.jar
```

### Frontend (Angular):
```bash
ng build --configuration production
# Deploy 'dist/' folder to web server
```

---

## 📞 Support

### Common Questions:

**Q: How do I reset the database?**
A: Just restart the backend - H2 is in-memory and resets automatically.

**Q: Can I add more quizzes?**
A: Yes! Login as admin and use Admin → Quizzes.

**Q: How are discounts calculated?**
A: Based on discount rules. Each rule checks user's points, badges, and quiz scores.

**Q: Can users take quizzes multiple times?**
A: Yes, but points are only awarded on the FIRST attempt.

**Q: What's the maximum discount?**
A: 50% off the base premium (hardcoded cap).

---

## 🎉 You're All Set!

Your gamified insurance platform is ready to use. Start by:
1. Taking quizzes to earn points
2. Unlocking badges
3. Getting premium discounts
4. Competing on the leaderboard!

**Happy Learning! 🚀**
