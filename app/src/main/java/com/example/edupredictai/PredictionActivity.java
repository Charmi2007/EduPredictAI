package com.example.edupredictai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class PredictionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        TextView predictedScoreText = findViewById(R.id.predictedScoreText);
        TextView performanceLevelText = findViewById(R.id.performanceLevelText);
        TextView insightText = findViewById(R.id.insightText);
        TextView weakAreasSummaryText = findViewById(R.id.weakAreasSummaryText);
        TextView recommendationsSummaryText = findViewById(R.id.recommendationsSummaryText);
        Button recommendationButton = findViewById(R.id.recommendationButton);
        Button homeButton = findViewById(R.id.homeButton);
        ImageView backToHomeIcon = findViewById(R.id.backToHomeIcon);

        // Get predicted score
        double predictedScore = getIntent().getDoubleExtra("predicted_score", 0.0);

        // Get personalized data
        ArrayList<String> weakAreas = getIntent().getStringArrayListExtra("weak_areas");
        ArrayList<String> recommendations = getIntent().getStringArrayListExtra("recommendations");
        String dailyGoal = getIntent().getStringExtra("daily_goal");

        // Display predicted score
        predictedScoreText.setText(String.format(Locale.getDefault(), "%.1f%%", predictedScore));

        // Determine performance level and customize insight
        if (predictedScore >= 85) {
            performanceLevelText.setText("Performance Level: Excellent 🌟");
            insightText.setText("Your predicted academic performance is outstanding! Your study rhythm and quiz performance are strong. Continue following your personalized daily study plan to maintain peak momentum.");
        } else if (predictedScore >= 70) {
            performanceLevelText.setText("Performance Level: Good 🎯");
            insightText.setText("Your predicted performance is solid with promising potential. Consistent review of previous exam topics and daily practice will help you advance to the highest performance bracket.");
        } else if (predictedScore >= 50) {
            performanceLevelText.setText("Performance Level: Average 📈");
            insightText.setText("You have a decent foundation, but targeted improvement is recommended. Focus on consistent daily study hours, taking quizzes frequently, and reinforcing topics you found challenging.");
        } else {
            performanceLevelText.setText("Performance Level: Needs Improvement ⚠️");
            insightText.setText("Your predicted performance highlights critical learning gaps. We recommend increasing daily dedicated study hours and completing focused learning activities to strengthen foundational concepts.");
        }

        // Preview Weak Areas & Recommendations
        if (weakAreasSummaryText != null) {
            if (weakAreas != null && !weakAreas.isEmpty()) {
                weakAreasSummaryText.setText("Focus Areas: " + String.join(", ", weakAreas));
            } else {
                weakAreasSummaryText.setText("Focus Areas: No major weakness detected — Great job!");
            }
        }

        if (recommendationsSummaryText != null) {
            if (recommendations != null && !recommendations.isEmpty()) {
                recommendationsSummaryText.setText("Top Action: " + recommendations.get(0));
            } else {
                recommendationsSummaryText.setText("Top Action: Continue your current learning routine.");
            }
        }

        // Open Personalized Learning screen
        recommendationButton.setOnClickListener(v -> {
            Intent intent = new Intent(PredictionActivity.this, LearningActivity.class);
            intent.putStringArrayListExtra("weak_areas", weakAreas);
            intent.putStringArrayListExtra("recommendations", recommendations);
            intent.putExtra("daily_goal", dailyGoal);
            startActivity(intent);
        });

        // Return to Dashboard
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> finish());
        }
        if (backToHomeIcon != null) {
            backToHomeIcon.setOnClickListener(v -> finish());
        }
    }
}