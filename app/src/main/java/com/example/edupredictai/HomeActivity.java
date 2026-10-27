package com.example.edupredictai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView greetingText;
    private TextView predictionText;
    private TextView performanceLevelText;
    private TextView predictionInsightBrief;
    private Button predictButton;
    private View learningButton;
    private View quizButton;
    private View analyticsButton;
    private View historyButton;
    private View profileButton;
    private ImageView headerProfileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NotificationHelper.createNotificationChannel(this);

        greetingText = findViewById(R.id.greetingText);
        predictionText = findViewById(R.id.predictionText);
        performanceLevelText = findViewById(R.id.performanceLevelText);
        predictionInsightBrief = findViewById(R.id.predictionInsightBrief);
        predictButton = findViewById(R.id.predictButton);
        learningButton = findViewById(R.id.learningButton);
        quizButton = findViewById(R.id.quizButton);
        analyticsButton = findViewById(R.id.analyticsButton);
        historyButton = findViewById(R.id.historyButton);
        profileButton = findViewById(R.id.profileButton);
        headerProfileIcon = findViewById(R.id.headerProfileIcon);

        // Predict button
        predictButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AcademicDataActivity.class);
            startActivity(intent);
        });

        // Personalized Learning button
        if (learningButton != null) {
            learningButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, LearningActivity.class);
                startActivity(intent);
            });
        }

        // Quiz button
        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, QuizActivity.class);
            startActivity(intent);
        });

        // Analytics button
        analyticsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        // History button
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Profile button
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        if (headerProfileIcon != null) {
            headerProfileIcon.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        NotificationHelper.showLearningReminder(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);
        String studentName = preferences.getString("student_name", "Student");
        int studentId = preferences.getInt("student_id", -1);

        greetingText.setText("Hello, " + studentName + "! 👋");

        // First check locally cached prediction
        float cachedScore = preferences.getFloat("predicted_score", -1);
        if (cachedScore >= 0) {
            updatePredictionCard(cachedScore);
        } else {
            // Check if backend has recent prediction for this student
            if (studentId != -1) {
                fetchLatestFromBackend(studentId);
            } else {
                updatePredictionCard(-1);
            }
        }
    }

    private void updatePredictionCard(double predictedScore) {
        if (predictedScore >= 0) {
            predictionText.setText(String.format(Locale.getDefault(), "%.1f%%", predictedScore));

            if (predictedScore >= 85) {
                performanceLevelText.setText("Performance Level: Excellent 🌟");
                performanceLevelText.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.accent_gold));
                predictionInsightBrief.setText("Outstanding academic momentum. Maintain your daily study consistency!");
            } else if (predictedScore >= 70) {
                performanceLevelText.setText("Performance Level: Good 🎯");
                performanceLevelText.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.accent_cyan));
                predictionInsightBrief.setText("Strong predicted outcome. Consistent quiz practice can push you into top tier.");
            } else if (predictedScore >= 50) {
                performanceLevelText.setText("Performance Level: Average 📈");
                performanceLevelText.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white));
                predictionInsightBrief.setText("Moderate performance. Increase daily study hours to improve weak subject areas.");
            } else {
                performanceLevelText.setText("Performance Level: Needs Improvement ⚠️");
                performanceLevelText.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.warning));
                predictionInsightBrief.setText("Action required: Review fundamentals and complete practice quizzes regularly.");
            }
            predictButton.setText("UPDATE PREDICTION");
        } else {
            predictionText.setText("--%");
            performanceLevelText.setText("Performance Level: Pending Evaluation");
            performanceLevelText.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white));
            predictionInsightBrief.setText("Tap below to evaluate your academic performance using our AI model.");
            predictButton.setText("PREDICT MY PERFORMANCE");
        }
    }

    private void fetchLatestFromBackend(int studentId) {
        ApiClient.getApiService().getAcademicData(studentId).enqueue(new Callback<AcademicResponse>() {
            @Override
            public void onResponse(Call<AcademicResponse> call, Response<AcademicResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AcademicResponse data = response.body();
                    double predictedScore = data.getPredicted_score();
                    updatePredictionCard(predictedScore);

                    // Sync to local cache
                    getSharedPreferences("EduPredictData", MODE_PRIVATE).edit()
                            .putFloat("predicted_score", (float) predictedScore)
                            .putFloat("previous_score", (float) data.getPrevious_score())
                            .putFloat("study_hours", (float) data.getStudy_hours())
                            .putFloat("quiz_average", (float) data.getQuiz_average())
                            .putInt("quizzes_attempted", data.getQuizzes_attempted())
                            .putFloat("previous_exam_score", (float) data.getPrevious_exam_score())
                            .putFloat("learning_activity", (float) data.getLearning_activity())
                            .apply();
                } else {
                    updatePredictionCard(-1);
                }
            }

            @Override
            public void onFailure(Call<AcademicResponse> call, Throwable t) {
                // If offline or failed, maintain current card state
            }
        });
    }
}