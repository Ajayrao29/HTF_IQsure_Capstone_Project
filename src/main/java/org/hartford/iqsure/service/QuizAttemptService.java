/*
 * ============================================================================
 * FILE: QuizAttemptService.java | LOCATION: service/
 * PURPOSE: THE CORE GAMIFICATION ENGINE. Handles quiz submission, scoring,
 *          point awarding, and badge checking. This is where the magic happens!
 *
 * KEY METHOD — submitQuiz():
 *   1. Gets correct answers from DB for the quiz
 *   2. Compares user's answers with correct answers → calculates score
 *   3. If FIRST attempt on this quiz → awards points (score × 10)
 *   4. Updates user's total points in the database
 *   5. Checks if user qualifies for any new badges (BadgeService)
 *   6. Returns result with score, percentage, points, and new badges
 *
 * CALLED BY: AttemptController.java
 * USES: QuizAttemptRepository, UserRepository, QuizRepository, AnswerRepository, BadgeService
 * ============================================================================
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.QuizSubmissionDTO;
import org.hartford.iqsure.dto.response.AttemptResponseDTO;
import org.hartford.iqsure.dto.response.BadgeResponseDTO;
import org.hartford.iqsure.entity.*;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final BadgeService badgeService;
    private final DiscountRuleRepository discountRuleRepository;
    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserBadgeRepository userBadgeRepository;

    @Transactional
    public AttemptResponseDTO submitQuiz(Long userId, QuizSubmissionDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + dto.getQuizId()));

        // Get correct answers for this quiz
        Map<Long, Integer> correctAnswers = answerRepository
                .findByQuestion_Quiz_QuizId(dto.getQuizId())
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getQuestion().getQuestionId(),
                        Answer::getRightOption,
                        (existing, replacement) -> replacement
                ));

        // Count correct answers and generate reports
        int score = 0;
        List<org.hartford.iqsure.dto.response.QuestionReportDTO> questionReports = new ArrayList<>();
        List<Question> allQuestions = questionRepository.findByQuiz_QuizId(dto.getQuizId());
        
        System.out.println("Quiz ID: " + dto.getQuizId());
        System.out.println("Submitted answers: " + dto.getAnswers());
        System.out.println("Correct answers DB: " + correctAnswers);

        for (Question q : allQuestions) {
            Long qId = q.getQuestionId();
            Integer selectedIdx = null;
            try {
                if (dto.getAnswers().containsKey(qId)) {
                    selectedIdx = Integer.valueOf(dto.getAnswers().get(qId).toString());
                } else if (dto.getAnswers().containsKey(qId.toString())) {
                    selectedIdx = Integer.valueOf(dto.getAnswers().get(qId.toString()).toString());
                }
            } catch (Exception e) {}

            boolean isCorrect = false;
            Integer correctIdx = correctAnswers.get(qId);
            if (correctIdx != null && correctIdx.equals(selectedIdx)) {
                score++;
                isCorrect = true;
            }

            String selectedAnswerText = "Not answered (" + selectedIdx + ")";
            // Extract options by splitting on pipe or comma, handling escaped pipes if necessary
            String rawOptions = q.getOptions();
            String[] opts = rawOptions.split("[\\|\\,]");
            
            // Clean individual option texts (remove A) B) etc.)
            List<String> cleanedOptsList = new java.util.ArrayList<>();
            for (String opt : opts) {
                String cleaned = opt.replaceFirst("^[A-Da-d][\\)\\.]\\s*", "").trim();
                if (!cleaned.isEmpty()) cleanedOptsList.add(cleaned);
            }
            String[] cleanedOpts = cleanedOptsList.toArray(new String[0]);
            
            if (selectedIdx != null && selectedIdx >= 0 && selectedIdx < cleanedOpts.length) {
                selectedAnswerText = cleanedOpts[selectedIdx];
            } else if (selectedIdx != null) {
                // If the index was too high, maybe the split failed? Let's show the whole options text for debugging
                selectedAnswerText = "Invalid index: " + selectedIdx + " for total options: " + cleanedOpts.length;
            }

            String correctAnswerText = "Unknown (Check Admin Panel)";
            if (correctIdx != null && correctIdx >= 0 && correctIdx < cleanedOpts.length) {
                correctAnswerText = cleanedOpts[correctIdx];
            }

            questionReports.add(org.hartford.iqsure.dto.response.QuestionReportDTO.builder()
                .questionText(q.getText())
                .selectedAnswer(selectedAnswerText)
                .correctAnswer(correctAnswerText)
                .explanation(q.getExplanation() != null ? q.getExplanation() : "No explanation provided.")
                .isCorrect(isCorrect)
                .build());
        }
        
        System.out.println("Calculated Score: " + score);

        int totalQuestions = allQuestions.size();

        // We want users to be able to recover points if they retake a quiz and get a better score!
        // Find their highest score on this quiz so far
        int previousBestScore = attemptRepository
                .findByQuiz_QuizId(dto.getQuizId())
                .stream()
                .filter(a -> a.getUser().getUserId().equals(userId))
                .map(QuizAttempt::getScore)
                .max(Integer::compareTo)
                .orElse(-1); // -1 means they've never taken it before

        int pointsEarned = 0;
        int newCorrectAnswers = score - Math.max(0, previousBestScore);
        
        if (newCorrectAnswers > 0) {
            // Give 10 base points for every NEW correct answer they got
            int basePoints = newCorrectAnswers * 10;
            
            // Calculate speed bonus (percentage increase applied to base points)
            int speedBonusPercent = dto.getSpeedBonus() != null ? dto.getSpeedBonus() : 0;
            int speedPoints = (int) (basePoints * (speedBonusPercent / 100.0));
            
            pointsEarned = basePoints + speedPoints;
            
            user.setUserPoints(user.getUserPoints() + pointsEarned);
            userRepository.save(user);
        }
        
        boolean isFirstAttempt = (previousBestScore == -1);

        // Save the attempt
        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .score(score)
                .totalQuestions(totalQuestions)
                .pointsEarned(pointsEarned)
                .attemptDate(LocalDateTime.now())
                .build();

        attempt = attemptRepository.save(attempt);

        // Check for newly unlocked badges
        List<BadgeResponseDTO> newBadges = isFirstAttempt
                ? badgeService.checkAndAwardBadges(userId)
                : List.of();

        // Auto-award discount rule rewards if user newly qualifies
        checkAndAwardDiscountRewards(user);

        double percentage = totalQuestions > 0 ? ((double) score / totalQuestions) * 100 : 0;

        return AttemptResponseDTO.builder()
                .attemptId(attempt.getAttemptId())
                .userId(userId)
                .quizId(dto.getQuizId())
                .quizTitle(quiz.getTitle())
                .score(score)
                .totalQuestions(totalQuestions)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .pointsEarned(pointsEarned)
                .attemptDate(attempt.getAttemptDate())
                .newBadgesUnlocked(newBadges)
                .questions(questionReports)
                .build();
    }

    public List<AttemptResponseDTO> getAttemptsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return attemptRepository.findByUser_UserIdOrderByAttemptDateDesc(userId)
                .stream().map(this::toDTO).toList();
    }

    public AttemptResponseDTO getAttemptById(Long attemptId) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found: " + attemptId));
        return toDTO(attempt);
    }

    private AttemptResponseDTO toDTO(QuizAttempt a) {
        double pct = a.getTotalQuestions() > 0
                ? ((double) a.getScore() / a.getTotalQuestions()) * 100 : 0;
        return AttemptResponseDTO.builder()
                .attemptId(a.getAttemptId())
                .userId(a.getUser().getUserId())
                .quizId(a.getQuiz().getQuizId())
                .quizTitle(a.getQuiz().getTitle())
                .score(a.getScore())
                .totalQuestions(a.getTotalQuestions())
                .percentage(Math.round(pct * 100.0) / 100.0)
                .pointsEarned(a.getPointsEarned())
                .attemptDate(a.getAttemptDate())
                .newBadgesUnlocked(List.of())
                .build();
    }
    /**
     * After a quiz attempt, checks ALL active discount rules to see if the user newly qualifies.
     * For each qualifying rule they haven't received a reward for yet → auto-create & grant the reward.
     */
    private void checkAndAwardDiscountRewards(User user) {
        Long userId = user.getUserId();
        int userPoints = user.getUserPoints();

        // FIX 1: Use the correct badge repository (not rewards)
        int badgeCount = userBadgeRepository.findByUser_UserId(userId).size();
        
        // Get user's best quiz score across all attempts
        double bestScore = attemptRepository
                .findByUser_UserIdOrderByAttemptDateDesc(userId)
                .stream()
                .mapToDouble(a -> a.getTotalQuestions() > 0
                        ? ((double) a.getScore() / a.getTotalQuestions()) * 100 : 0)
                .max().orElse(0.0);

        System.out.println("[RewardCheck] userId=" + userId
                + " points=" + userPoints + " badges=" + badgeCount + " bestScore=" + bestScore);

        List<DiscountRule> rules = discountRuleRepository.findByIsActiveTrue();
        for (DiscountRule rule : rules) {
            // FIX 2: Badge condition was missing the actual comparison
            boolean meetsConditions =
                    (rule.getMinUserPoints() <= 0        || userPoints  >= rule.getMinUserPoints()) &&
                    (rule.getMinQuizScorePercent() <= 0  || bestScore   >= rule.getMinQuizScorePercent()) &&
                    (rule.getMinBadgesEarned() <= 0      || badgeCount  >= rule.getMinBadgesEarned());

            System.out.println("[RewardCheck] Rule '" + rule.getRuleName() + "' meets=" + meetsConditions);

            if (!meetsConditions) continue;

            // Check if the user already has a reward for this rule (to avoid duplicates)
            String rewardLabel = "Discount: " + rule.getRuleName();
            boolean alreadyHas = userRewardRepository.findByUser_UserId(userId)
                    .stream()
                    .anyMatch(ur -> ur.getReward().getRewardType().equals(rewardLabel));

            if (!alreadyHas) {
                // Create the reward in the rewards table
                Reward reward = rewardRepository.save(Reward.builder()
                        .rewardType(rewardLabel)
                        .discountValue(rule.getDiscountPercentage())
                        .expiryDate(LocalDate.now().plusMonths(6))
                        .build());

                // FIX 3: Lombok boolean 'isXxx' builder method is 'xxx()', not 'isXxx()'
                UserReward userReward = UserReward.builder()
                        .user(user)
                        .reward(reward)
                        .redeemedDate(LocalDateTime.now())
                        .build();
                // isUsed defaults to false via @Builder.Default so we don't need to set it
                userRewardRepository.save(userReward);

                System.out.println("[RewardCheck] ✅ Awarded '" + rewardLabel + "' to user " + userId);
            } else {
                System.out.println("[RewardCheck] already has '" + rewardLabel + "'");
            }
        }
    }
}
