package com.example.edupredictai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LearningActivity extends AppCompatActivity {

    private TextView weakAreasText;
    private TextView recommendationsText;
    private TextView studyGoalText;
    private Button quizActionButton;
    private Button dashboardButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        weakAreasText = findViewById(R.id.weakAreasText);
        recommendationsText = findViewById(R.id.recommendationsText);
        studyGoalText = findViewById(R.id.studyGoalText);
        quizActionButton = findViewById(R.id.quizActionButton);
        dashboardButton = findViewById(R.id.dashboardButton);
        backButton = findViewById(R.id.backButton);

        // Get data from Intent if available
        ArrayList<String> weakAreas = getIntent().getStringArrayListExtra("weak_areas");
        ArrayList<String> recommendations = getIntent().getStringArrayListExtra("recommendations");
        String dailyGoal = getIntent().getStringExtra("daily_goal");

        // If opened directly from HomeActivity, compute from cached academic metrics
        if ((weakAreas == null || weakAreas.isEmpty()) && (recommendations == null || recommendations.isEmpty())) {
            loadFromCachedData();
        } else {
            displayPersonalizedPlan(weakAreas, recommendations, dailyGoal);
        }

        // Navigation actions
        if (quizActionButton != null) {
            quizActionButton.setOnClickListener(v -> {
                Intent intent = new Intent(LearningActivity.this, QuizActivity.class);
                startActivity(intent);
            });
        }

        if (dashboardButton != null) {
            dashboardButton.setOnClickListener(v -> finish());
        }

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void loadFromCachedData() {
        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);

        float quizAverage = preferences.getFloat("quiz_average", -1);
        float studyHours = preferences.getFloat("study_hours", -1);
        float previousExam = preferences.getFloat("previous_exam_score", -1);
        float learningActivity = preferences.getFloat("learning_activity", -1);
        float predictedScore = preferences.getFloat("predicted_score", -1);

        ArrayList<String> weakAreas = new ArrayList<>();
        ArrayList<String> recommendations = new ArrayList<>();

        if (quizAverage >= 0 && quizAverage < 70) {
            weakAreas.add("Quiz Performance");
            recommendations.add("Practice quizzes for 30 minutes every day");
        }
        if (previousExam >= 0 && previousExam < 70) {
            weakAreas.add("Exam Performance");
            recommendations.add("Review topics from your previous exam");
        }
        if (studyHours >= 0 && studyHours < 3) {
            weakAreas.add("Study Routine");
            recommendations.add("Increase your daily study time");
        }
        if (learningActivity >= 0 && learningActivity < 70) {
            weakAreas.add("Learning Activity");
            recommendations.add("Practice more interactive learning activities");
        }

        if (weakAreas.isEmpty()) {
            weakAreas.add("No major weakness detected");
            recommendations.add("Continue your current learning routine and maintain daily consistency.");
        }

        String dailyGoal;
        if (predictedScore >= 85) {
            dailyGoal = "2 Hours / Day";
        } else if (predictedScore >= 70) {
            dailyGoal = "2.5 Hours / Day";
        } else {
            dailyGoal = "3 Hours / Day";
        }

        displayPersonalizedPlan(weakAreas, recommendations, dailyGoal);
    }

    private void displayPersonalizedPlan(ArrayList<String> weakAreas, ArrayList<String> recommendations, String dailyGoal) {
        // Display weak areas
        if (weakAreas != null && !weakAreas.isEmpty()) {
            StringBuilder weakAreaText = new StringBuilder();
            for (String area : weakAreas) {
                weakAreaText.append("• ").append(area).append("\n");
            }
            weakAreasText.setText(weakAreaText.toString().trim());
        } else {
            weakAreasText.setText("• No major weakness detected — Excellent!");
        }

        // Display recommendations
        if (recommendations != null && !recommendations.isEmpty()) {
            StringBuilder recommendationText = new StringBuilder();
            for (String rec : recommendations) {
                recommendationText.append("✓ ").append(rec).append("\n\n");
            }
            recommendationsText.setText(recommendationText.toString().trim());
        } else {
            recommendationsText.setText("✓ Continue your current learning routine and review notes regularly.");
        }

        // Display daily study goal
        if (dailyGoal != null && !dailyGoal.isEmpty()) {
            studyGoalText.setText(dailyGoal);
        } else {
            studyGoalText.setText("2.5 Hours / Day");
        }
    }
}