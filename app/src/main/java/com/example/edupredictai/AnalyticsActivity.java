package com.example.edupredictai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView previousScoreText;
    private TextView quizAverageText;
    private TextView studyHoursText;
    private TextView predictedScoreText;
    private TextView learningActivityText;
    private TextView progressText;
    private ProgressBar analyticsProgressBar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        previousScoreText = findViewById(R.id.previousScoreText);
        quizAverageText = findViewById(R.id.quizAverageText);
        studyHoursText = findViewById(R.id.studyHoursText);
        predictedScoreText = findViewById(R.id.predictedScoreText);
        learningActivityText = findViewById(R.id.learningActivityText);
        progressText = findViewById(R.id.progressText);
        analyticsProgressBar = findViewById(R.id.analyticsProgressBar);
        backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);
        int studentId = preferences.getInt("student_id", -1);

        if (studentId == -1) {
            Toast.makeText(this, "Please login to view analytics.", Toast.LENGTH_SHORT).show();
            displayCachedFallback(preferences);
            return;
        }

        if (analyticsProgressBar != null) {
            analyticsProgressBar.setVisibility(View.VISIBLE);
        }

        ApiClient.getApiService()
                .getAcademicData(studentId)
                .enqueue(new Callback<AcademicResponse>() {
                    @Override
                    public void onResponse(Call<AcademicResponse> call, Response<AcademicResponse> response) {
                        if (analyticsProgressBar != null) {
                            analyticsProgressBar.setVisibility(View.GONE);
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            AcademicResponse data = response.body();

                            if (data.isSuccess()) {
                                displayAnalytics(data);
                            } else {
                                // No record on backend yet, fallback to local cached values
                                displayCachedFallback(preferences);
                            }
                        } else {
                            displayCachedFallback(preferences);
                        }
                    }

                    @Override
                    public void onFailure(Call<AcademicResponse> call, Throwable t) {
                        if (analyticsProgressBar != null) {
                            analyticsProgressBar.setVisibility(View.GONE);
                        }
                        // Offline or connection error, use locally cached data
                        displayCachedFallback(preferences);
                        Toast.makeText(
                                AnalyticsActivity.this,
                                "Showing cached analytics (Server connection unavailable).",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void displayCachedFallback(SharedPreferences preferences) {
        float prevScore = preferences.getFloat("previous_score", -1);
        float quizAvg = preferences.getFloat("quiz_average", -1);
        float studyHrs = preferences.getFloat("study_hours", -1);
        float learnAct = preferences.getFloat("learning_activity", -1);
        float predScore = preferences.getFloat("predicted_score", -1);

        if (prevScore >= 0) {
            previousScoreText.setText(String.format(Locale.getDefault(), "%.1f%%", prevScore));
        } else {
            previousScoreText.setText("N/A");
        }

        if (quizAvg >= 0) {
            quizAverageText.setText(String.format(Locale.getDefault(), "%.1f%%", quizAvg));
        } else {
            quizAverageText.setText("N/A");
        }

        if (studyHrs >= 0) {
            studyHoursText.setText(String.format(Locale.getDefault(), "%.1f hrs", studyHrs));
        } else {
            studyHoursText.setText("N/A");
        }

        if (learnAct >= 0) {
            learningActivityText.setText(String.format(Locale.getDefault(), "%.1f%%", learnAct));
        } else {
            learningActivityText.setText("N/A");
        }

        if (predScore >= 0) {
            predictedScoreText.setText(String.format(Locale.getDefault(), "%.1f%%", predScore));

            if (prevScore >= 0) {
                double diff = predScore - prevScore;
                if (diff > 0) {
                    progressText.setText(String.format(
                            Locale.getDefault(),
                            "Predicted Improvement: +%.1f%%\n\nExcellent momentum! Following your daily study plan will maintain this upward trend.",
                            diff
                    ));
                } else if (diff < 0) {
                    progressText.setText(String.format(
                            Locale.getDefault(),
                            "Predicted Variation: %.1f%%\n\nSlight decline expected. Increase daily study hours to reverse this trend.",
                            diff
                    ));
                } else {
                    progressText.setText("Predicted performance is in line with your historical baseline score.");
                }
            } else {
                progressText.setText("Prediction generated successfully. Consistent practice will help you improve.");
            }
        } else {
            predictedScoreText.setText("--%");
            progressText.setText("No academic evaluations recorded yet. Run a prediction to see your analytics.");
        }
    }

    private void displayAnalytics(AcademicResponse data) {
        double previousScore = data.getPrevious_score();
        double quizAverage = data.getQuiz_average();
        double studyHours = data.getStudy_hours();
        double predictedScore = data.getPredicted_score();
        double learningActivity = data.getLearning_activity();

        previousScoreText.setText(String.format(Locale.getDefault(), "%.1f%%", previousScore));
        quizAverageText.setText(String.format(Locale.getDefault(), "%.1f%%", quizAverage));
        studyHoursText.setText(String.format(Locale.getDefault(), "%.1f hrs", studyHours));
        predictedScoreText.setText(String.format(Locale.getDefault(), "%.1f%%", predictedScore));
        learningActivityText.setText(String.format(Locale.getDefault(), "%.1f%%", learningActivity));

        double improvement = predictedScore - previousScore;
        if (improvement > 0) {
            progressText.setText(String.format(
                    Locale.getDefault(),
                    "Predicted Improvement: +%.1f%%\n\nYour trajectory is positive! Keep following your personalized study recommendations.",
                    improvement
            ));
        } else if (improvement < 0) {
            progressText.setText(String.format(
                    Locale.getDefault(),
                    "Predicted Trajectory: %.1f%%\n\nPotential performance drop detected. Focus on your recommended focus areas to boost retention.",
                    improvement
            ));
        } else {
            progressText.setText("Your predicted performance matches your historical score baseline.");
        }
    }
}