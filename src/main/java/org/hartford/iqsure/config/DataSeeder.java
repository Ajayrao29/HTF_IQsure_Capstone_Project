/*
 * ============================================================================
 * FILE: DataSeeder.java
 * LOCATION: src/main/java/org/hartford/iqsure/config/
 * PURPOSE: Automatically creates a default ADMIN user when the app starts
 *          for the FIRST TIME (when the database is empty).
 *          This ensures there's always an admin who can log in and set up
 *          quizzes, policies, badges, etc. through the admin panel.
 *
 * HOW IT WORKS:
 *   - @PostConstruct → Spring calls seedAdmin() after this bean is created
 *     and all dependencies (userRepository, passwordEncoder) are injected
 *   - Checks if any users exist in the database
 *   - If NO users exist → creates an admin with email "admin@iqsure.com"
 *   - If users already exist → does nothing (skips seeding)
 *
 * DEFAULT ADMIN CREDENTIALS:
 *   - Email:    admin@iqsure.com
 *   - Password: admin123
 *
 * ANNOTATIONS EXPLAINED:
 *   - @Slf4j (Lombok) → Auto-creates a "log" variable for printing messages
 *   - @Component → Tells Spring: "create an instance of this class automatically"
 *   - @RequiredArgsConstructor (Lombok) → Auto-creates constructor for 'final' fields
 *   - @PostConstruct → Method runs once after bean initialization (replaces CommandLineRunner)
 *
 * CONNECTS TO:
 *   - UserRepository.java (repository/) → to check user count and save admin
 *   - SecurityConfig.java (config/) → provides the PasswordEncoder for hashing
 *   - User.java (entity/) → the User entity that gets saved to the "users" table
 * ============================================================================
 */
package org.hartford.iqsure.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.hartford.iqsure.repository.EducationContentRepository educationContentRepository;
    private final org.hartford.iqsure.repository.QuizRepository quizRepository;
    private final org.hartford.iqsure.repository.QuestionRepository questionRepository;
    private final org.hartford.iqsure.repository.AnswerRepository answerRepository;
    private final org.hartford.iqsure.repository.PolicyRepository policyRepository;
    private final org.hartford.iqsure.repository.BadgeRepository badgeRepository;
    private final org.hartford.iqsure.repository.RewardRepository rewardRepository;
    private final org.hartford.iqsure.repository.DiscountRuleRepository discountRuleRepository;
    private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    // This method runs automatically after the bean is created and dependencies are injected
    @PostConstruct
    public void seedAdmin() {
        // Seed Admin if not exists
        if (userRepository.findByEmail("admin@iqsure.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@iqsure.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("1234567890")
                    .role(User.Role.ROLE_ADMIN)
                    .userPoints(0)
                    .build());
            log.info("Admin user created: admin@iqsure.com / admin123");
        }

        // Seed Underwriter if not exists
        if (userRepository.findByEmail("alice@iqsure.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Alice Underwriter")
                    .email("alice@iqsure.com")
                    .password(passwordEncoder.encode("uw123"))
                    .role(User.Role.ROLE_UNDERWRITER)
                    .licenseNumber("UW-88990")
                    .specialization("HEALTH")
                    .commissionPercentage(new java.math.BigDecimal("5.5"))
                    .status("ACTIVE")
                    .build());
            log.info("Underwriter created: alice@iqsure.com / uw123");
        }

        // Seed Claims Officer if not exists
        if (userRepository.findByEmail("bob@iqsure.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Bob Claims")
                    .email("bob@iqsure.com")
                    .password(passwordEncoder.encode("claims123"))
                    .role(User.Role.ROLE_CLAIMS_OFFICER)
                    .employeeId("EMP-1234")
                    .department("CLAIMS")
                    .approvalLimit(new java.math.BigDecimal("750000.00"))
                    .status("ACTIVE")
                    .build());
            log.info("Claims Officer created: bob@iqsure.com / claims123");
        }

        // Seed Default User if not exists
        if (userRepository.findByEmail("user@iqsure.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("John Doe")
                    .email("user@iqsure.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(User.Role.ROLE_USER)
                    .phone("9876543210")
                    .userPoints(500)
                    .city("New York")
                    .state("NY")
                    .status("ACTIVE")
                    .build());
            log.info("Default user created: user@iqsure.com / user123");
        }

        if (educationContentRepository.count() == 0) {
            seedEducationContent();
        }

        if (policyRepository.count() == 0) {
            seedPolicies();
        }

        if (badgeRepository.count() == 0) {
            seedBadges();
        }

        if (rewardRepository.count() == 0) {
            seedRewards();
        }

        // Always seed quizzes if questions are missing or if we want to ensure the 3 default quizzes exist
        if (quizRepository.count() < 3 || questionRepository.count() == 0) {
            seedQuizzes();
        }

        if (discountRuleRepository.count() == 0) {
            seedDiscountRules();
        }
    }
    private void seedEducationContent() {
        // English
        saveEduContent("insurance_basics", "en", "Insurance Basics",
                "Insurance is a financial protection system where an individual pays a premium to an insurance company in exchange for protection against financial losses caused by unexpected events such as accidents, illness, or property damage.\n\nKey Terms:\n\nPremium\nAmount paid periodically for insurance coverage.\n\nDeductible\nAmount the policyholder must pay before the insurance company starts covering expenses.\n\nCoverage\nMaximum amount an insurance company will pay for a covered loss.");
        saveEduContent("health_insurance", "en", "Health Insurance",
                "Health insurance covers the cost of medical care. It provides financial protection against high health care costs.");
        saveEduContent("vehicle_insurance", "en", "Vehicle Insurance",
                "Vehicle insurance protects you against financial loss in the event of an accident or theft of your vehicle.");
        saveEduContent("life_insurance", "en", "Life Insurance",
                "Life insurance guarantees the insurer pays a sum of money to named beneficiaries when the insured dies.");
        saveEduContent("claims_process", "en", "Claims Process",
                "The claims process includes reporting an incident, documentation, investigation, and settlement by the insurer.");
        saveEduContent("premiums_and_deductibles", "en", "Premiums and Deductibles",
                "A premium is what you pay to keep your policy active. A deductible is what you pay out-of-pocket before insurance covers a claim.");

        // Spanish
        saveEduContent("insurance_basics", "es", "Conceptos Básicos de Seguros",
                "El seguro es un sistema de protección financiera en el que un individuo paga una prima a una compañía de seguros a cambio de protección contra pérdidas financieras...");
        
        // Hindi
        saveEduContent("insurance_basics", "hi", "बीमा की मूल बातें",
                "बीमा एक वित्तीय सुरक्षा प्रणाली है जहाँ एक व्यक्ति अप्रत्याशित घटनाओं के कारण होने वाले वित्तीय नुकसान से सुरक्षा के बदले में बीमा कंपनी को प्रीमियम का भुगतान करता है।...");

        // Telugu
        saveEduContent("insurance_basics", "te", "బీమా ప్రాథమిక అంశాలు",
                "బీమా అనేది ఊహించని సంఘటనల వలన కలిగే ఆర్థిక నష్టాల నుండి రక్షణ కోసం బీమా సంస్థకు ప్రీమియం చెల్లించే ఆర్థిక రక్షణ వ్యవస్థ.");
        
        // Kannada
        saveEduContent("insurance_basics", "kn", "ವಿಮಾ ಮೂಲಭೂತ ಅಂಶಗಳು",
                "ವಿಮೆಯು ಆರ್ಥಿಕ ರಕ್ಷಣಾ ವ್ಯವಸ್ಥೆಯಾಗಿದ್ದು, ಇಲ್ಲಿ ಒಬ್ಬ ವ್ಯಕ್ತಿಯು ಅನಿರೀಕ್ಷಿತ ಘಟನೆಗಳಿಂದ ಉಂಟಾಗುವ ಆರ್ಥಿಕ ನಷ್ಟಗಳ ವಿರುದ್ಧ ರಕ್ಷಣೆಗಾಗಿ ವಿಮಾ ಕಂಪನಿಗೆ ಪ್ರೀಮಿಯಂ ಪಾವತಿಸುತ್ತಾನೆ.");
                
        log.info("Education content seeded.");
    }

    private void saveEduContent(String topic, String lang, String title, String content) {
        educationContentRepository.save(org.hartford.iqsure.entity.EducationContent.builder()
                .topic(topic)
                .language(lang)
                .title(title)
                .content(content)
                .build());
    }

    private void seedPolicies() {
        // Clear existing policies to fulfill "removeing all policies" request
        // policyRepository.deleteAll(); // Keep this optional if we want to reset every time

        savePolicy("Basic Health Plan", "Essential health coverage for individuals with basic hospitalization, day-care procedures, and ambulance charges.", 5000.0, 300000.0, 12, "18-65", "INDIVIDUAL", "1 month", false, false);
        savePolicy("Silver Health Plan", "Enhanced coverage with higher sum insured, cashless treatment at 5000+ network hospitals, and pre/post hospitalization coverage.", 8500.0, 500000.0, 12, "18-65", "INDIVIDUAL", "2 months", false, false);
        savePolicy("Gold Health Plan", "Premium comprehensive coverage including maternity benefits, pre-existing disease cover after waiting period.", 15000.0, 1000000.0, 12, "18-65", "INDIVIDUAL", "3 months", true, true);
        savePolicy("Family Health Plan", "Comprehensive family floater plan covering spouse and up to 3 children. Includes maternity and restoration benefit.", 20000.0, 1500000.0, 12, "18-65", "FAMILY", "2 months", true, false);
        savePolicy("Senior Citizen Plan", "Tailored plan for senior citizens aged 60-80 years. Covers pre-existing diseases after 2-year waiting period.", 25000.0, 800000.0, 12, "60-80", "SENIOR_CITIZEN", "24 months", false, true);
        savePolicy("Platinum Health Plan", "Ultimate individual plan with maximum ₹20 Lakh coverage, air ambulance, and personal accident cover.", 30000.0, 2000000.0, 12, "18-55", "INDIVIDUAL", "1 month", true, true);

        log.info("Health Insurance Policies seeded.");
    }

    private void savePolicy(String title, String desc, Double premium, Double coverage, Integer duration, String age, String type, String waiting, Boolean maternity, Boolean preExisting) {
        policyRepository.save(org.hartford.iqsure.entity.Policy.builder()
                .title(title)
                .description(desc)
                .basePremium(premium)
                .coverageAmount(coverage)
                .durationMonths(duration)
                .policyType(org.hartford.iqsure.entity.Policy.PolicyType.HEALTH)
                .ageRange(age)
                .planType(type)
                .waitingPeriod(waiting)
                .hasMaternityCover(maternity)
                .hasPreExistingCover(preExisting)
                .isActive(true)
                .build());
    }

    private void seedBadges() {
        badgeRepository.save(org.hartford.iqsure.entity.Badge.builder().name("Quick Learner").description("Complete your first quiz").reqPoints(100).icon("🎓").build());
        badgeRepository.save(org.hartford.iqsure.entity.Badge.builder().name("Insurance Pro").description("Score 100% in any quiz").reqPoints(300).icon("🔍").build());
        badgeRepository.save(org.hartford.iqsure.entity.Badge.builder().name("Loyal Member").description("Hold an active policy for 1 month").reqPoints(500).icon("🛡️").build());
        badgeRepository.save(org.hartford.iqsure.entity.Badge.builder().name("Claim Hero").description("Successfully settle your first claim").reqPoints(1000).icon("🏆").build());
        log.info("Badges seeded.");
    }

    private void seedRewards() {
        java.time.LocalDate nextYear = java.time.LocalDate.now().plusYears(1);
        rewardRepository.save(org.hartford.iqsure.entity.Reward.builder().rewardType("CASHBACK").discountValue(10.0).reqPoints(200).description("10% Cashback on next premium pulse").expiryDate(nextYear).build());
        rewardRepository.save(org.hartford.iqsure.entity.Reward.builder().rewardType("DISCOUNT").discountValue(15.0).reqPoints(400).description("15% Discount on any health policy").expiryDate(nextYear).build());
        rewardRepository.save(org.hartford.iqsure.entity.Reward.builder().rewardType("GIFT_CARD").discountValue(500.0).reqPoints(600).description("₹500 Health Pharmacy Gift Card").expiryDate(nextYear).build());
        log.info("Rewards seeded.");
    }

    private void seedQuizzes() {
        // Clear existing to avoid duplicates or empty quizzes
        answerRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();

        // Quiz 1: Health Insurance Basics
        org.hartford.iqsure.entity.Quiz healthBasics = quizRepository.save(org.hartford.iqsure.entity.Quiz.builder()
                .title("Health Insurance Fundamentals")
                .category("HEALTH")
                .difficulty(org.hartford.iqsure.entity.Quiz.Difficulty.EASY)
                .build());

        addQuestion(healthBasics, "What is a 'premium' in insurance?", 
                "A free gift,The monthly/annual payment to keep coverage,The amount you pay when a claim is made,A type of policy",
                1, "A premium is the amount paid periodically (monthly or annually) to keep the insurance policy active.");

        addQuestion(healthBasics, "Which of these is NOT typically covered by standard health insurance?", 
                "Hospitalization,Emergency surgery,Cosmetic surgery,Doctor visits",
                2, "Most standard health insurance plans exclude cosmetic procedures that are not medically necessary.");

        addQuestion(healthBasics, "What does 'Network Hospital' mean?", 
                "A hospital with internet,A hospital where you can get cashless treatment,A government hospital,A private clinic",
                1, "Network hospitals have partner agreements with insurers to provide cashless settlement or discounted rates.");

        // Quiz 2: Life Insurance
        org.hartford.iqsure.entity.Quiz lifeIns = quizRepository.save(org.hartford.iqsure.entity.Quiz.builder()
                .title("Life Insurance Essentials")
                .category("LIFE")
                .difficulty(org.hartford.iqsure.entity.Quiz.Difficulty.MEDIUM)
                .build());

        addQuestion(lifeIns, "Which type of life insurance provides coverage for a specific period?", 
                "Whole Life,Term Life,Universal Life,Variable Life",
                1, "Term insurance provides coverage for a specific term (e.g., 20 years). If the insured stays alive, there is typically no payout.");

        addQuestion(lifeIns, "Who is a 'Beneficiary'?", 
                "The person who sells the insurance,The person who receives the money if the insured dies,The doctor,The insurance company manager",
                1, "The beneficiary is the person or entity designated to receive the death benefit payout.");

        addQuestion(lifeIns, "What is a 'Nominee' in insurance?", 
                "The person who pays the premium,The person designated to receive benefits,The insurance agent,The policy issuer",
                1, "A nominee is the person appointed by the policyholder to receive the sum assured in case of the policyholder's death.");

        // Quiz 3: Claims & Coverage
        org.hartford.iqsure.entity.Quiz claimsQuiz = quizRepository.save(org.hartford.iqsure.entity.Quiz.builder()
                .title("Claims & Policy Coverage")
                .category("CLAIMS")
                .difficulty(org.hartford.iqsure.entity.Quiz.Difficulty.HARD)
                .build());

        addQuestion(claimsQuiz, "What does 'Deductible' mean?", 
                "The amount the insurer pays,The amount you pay out-of-pocket before insurance kicks in,The total coverage amount,A discount on the premium",
                1, "A deductible is the fixed amount you must pay before your insurance company starts to pay for a covered loss.");

        addQuestion(claimsQuiz, "What is 'Cashless Settlement'?", 
                "Paying in cash at the hospital,The insurer paying the hospital directly,Getting a refund after paying,A type of banking service",
                1, "In cashless settlement, the insurance company settles the bill directly with the network hospital, so you don't have to pay from your pocket.");

        addQuestion(claimsQuiz, "What is 'Sum Insured'?", 
                "The number of people covered,The maximum amount the insurer will pay in a year,The total premium paid,The value of the hospital building",
                1, "Sum Insured is the maximum amount an insurance company will pay for a covered loss in a policy year.");

        log.info("3 Quizzes and their questions seeded successfully.");
    }

    private void addQuestion(org.hartford.iqsure.entity.Quiz quiz, String text, String options, int rightOptionIdx, String explanation) {
        org.hartford.iqsure.entity.Question q = questionRepository.save(org.hartford.iqsure.entity.Question.builder()
                .quiz(quiz)
                .text(text)
                .options(options)
                .explanation(explanation)
                .build());

        String[] optArr = options.split(",");
        answerRepository.save(org.hartford.iqsure.entity.Answer.builder()
                .question(q)
                .text(optArr[rightOptionIdx])
                .rightOption(rightOptionIdx)
                .build());
    }

    private void seedDiscountRules() {
        discountRuleRepository.save(org.hartford.iqsure.entity.DiscountRule.builder()
                .ruleName("Beginner Learner Discount")
                .description("Get 5% off for starting your learning journey. Req: 100 points.")
                .minUserPoints(100)
                .discountPercentage(5.0)
                .isActive(true)
                .build());

        discountRuleRepository.save(org.hartford.iqsure.entity.DiscountRule.builder()
                .ruleName("Quiz Whiz Reward")
                .description("Get 10% off for scoring 80%+ on any quiz and having 200 points.")
                .minQuizScorePercent(80.0)
                .minUserPoints(200)
                .discountPercentage(10.0)
                .isActive(true)
                .build());

        discountRuleRepository.save(org.hartford.iqsure.entity.DiscountRule.builder()
                .ruleName("Badge Collector Bonus")
                .description("Get 15% off for earning 3 badges and 500 points.")
                .minBadgesEarned(3)
                .minUserPoints(500)
                .discountPercentage(15.0)
                .isActive(true)
                .build());

        discountRuleRepository.save(org.hartford.iqsure.entity.DiscountRule.builder()
                .ruleName("Health Policy Expert")
                .description("Get 20% off Health Policies. Req: 90% quiz score.")
                .minQuizScorePercent(90.0)
                .applicablePolicyType(org.hartford.iqsure.entity.Policy.PolicyType.HEALTH)
                .discountPercentage(20.0)
                .isActive(true)
                .build());

        discountRuleRepository.save(org.hartford.iqsure.entity.DiscountRule.builder()
                .ruleName("Elite Insurance Protector")
                .description("Top-tier 25% discount for 1000+ points and all 4 badges.")
                .minUserPoints(1000)
                .minBadgesEarned(4)
                .discountPercentage(25.0)
                .isActive(true)
                .build());

        log.info("Discount rules seeded.");
    }
}

