package com.example.edupredictai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcademicDataActivity extends AppCompatActivity {

    private EditText previousScoreEditText;
    private EditText studyHoursEditText;
    private EditText quizAverageEditText;
    private EditText quizzesAttemptedEditText;
    private EditText previousExamEditText;
    private EditText learningActivityEditText;
    private Button continueButton;
    private ProgressBar academicProgressBar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_data);

        previousScoreEditText = findViewById(R.id.previousScoreEditText);
        studyHoursEditText = findViewById(R.id.studyHoursEditText);
        quizAverageEditText = findViewById(R.id.quizAverageEditText);
        quizzesAttemptedEditText = findViewById(R.id.quizzesAttemptedEditText);
        previousExamEditText = findViewById(R.id.previousExamEditText);
        learningActivityEditText = findViewById(R.id.learningActivityEditText);
        continueButton = findViewById(R.id.continueButton);
        academicProgressBar = findViewById(R.id.academicProgressBar);
        backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Load existing saved quiz or academic results if available
        loadExistingData();

        continueButton.setOnClickListener(v -> validateAndSendRequest());
    }

    private void loadExistingData() {
        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);

        float quizAverage = preferences.getFloat("quiz_average", -1);
        int quizzesAttempted = preferences.getInt("quizzes_attempted", -1);
        float previousScore = preferences.getFloat("previous_score", -1);
        float studyHours = preferences.getFloat("study_hours", -1);
        float previousExam = preferences.getFloat("previous_exam_score", -1);
        float learningActivity = preferences.getFloat("learning_activity", -1);

        if (quizAverage >= 0) {
            quizAverageEditText.setText(String.valueOf(quizAverage));
        }
        if (quizzesAttempted >= 0) {
            quizzesAttemptedEditText.setText(String.valueOf(quizzesAttempted));
        }
        if (previousScore >= 0 && previousScoreEditText.getText().toString().isEmpty()) {
            previousScoreEditText.setText(String.valueOf(previousScore));
        }
        if (studyHours >= 0 && studyHoursEditText.getText().toString().isEmpty()) {
            studyHoursEditText.setText(String.valueOf(studyHours));
        }
        if (previousExam >= 0 && previousExamEditText.getText().toString().isEmpty()) {
            previousExamEditText.setText(String.valueOf(previousExam));
        }
        if (learningActivity >= 0 && learningActivityEditText.getText().toString().isEmpty()) {
            learningActivityEditText.setText(String.valueOf(learningActivity));
        }
    }

    private void validateAndSendRequest() {
        String prevScoreStr = previousScoreEditText.getText().toString().trim();
        String studyHoursStr = studyHoursEditText.getText().toString().trim();
        String quizAvgStr = quizAverageEditText.getText().toString().trim();
        String quizzesAttStr = quizzesAttemptedEditText.getText().toString().trim();
        String prevExamStr = previousExamEditText.getText().toString().trim();
        String learningActStr = learningActivityEditText.getText().toString().trim();

        if (prevScoreStr.isEmpty()) {
            previousScoreEditText.setError("Please enter previous score");
            previousScoreEditText.requestFocus();
            return;
        }
        if (studyHoursStr.isEmpty()) {
            studyHoursEditText.setError("Please enter study hours");
            studyHoursEditText.requestFocus();
            return;
        }
        if (quizAvgStr.isEmpty()) {
            quizAverageEditText.setError("Please enter quiz average");
            quizAverageEditText.requestFocus();
            return;
        }
        if (quizzesAttStr.isEmpty()) {
            quizzesAttemptedEditText.setError("Please enter quizzes attempted");
            quizzesAttemptedEditText.requestFocus();
            return;
        }
        if (prevExamStr.isEmpty()) {
            previousExamEditText.setError("Please enter previous exam score");
            previousExamEditText.requestFocus();
            return;
        }
        if (learningActStr.isEmpty()) {
            learningActivityEditText.setError("Please enter learning activity score");
            learningActivityEditText.requestFocus();
            return;
        }

        double previousScore;
        double studyHours;
        double quizAverage;
        int quizzesAttempted;
        double previousExam;
        double learningActivity;

        try {
            previousScore = Double.parseDouble(prevScoreStr);
            studyHours = Double.parseDouble(studyHoursStr);
            quizAverage = Double.parseDouble(quizAvgStr);
            quizzesAttempted = Integer.parseInt(quizzesAttStr);
            previousExam = Double.parseDouble(prevExamStr);
            learningActivity = Double.parseDouble(learningActStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric values in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate reasonable ranges
        if (previousScore < 0 || previousScore > 100) {
            previousScoreEditText.setError("Score must be between 0 and 100");
            previousScoreEditText.requestFocus();
            return;
        }
        if (studyHours < 0 || studyHours > 24) {
            studyHoursEditText.setError("Study hours must be between 0 and 24");
            studyHoursEditText.requestFocus();
            return;
        }
        if (quizAverage < 0 || quizAverage > 100) {
            quizAverageEditText.setError("Quiz average must be between 0 and 100");
            quizAverageEditText.requestFocus();
            return;
        }
        if (quizzesAttempted < 0) {
            quizzesAttemptedEditText.setError("Must be 0 or greater");
            quizzesAttemptedEditText.requestFocus();
            return;
        }
        if (previousExam < 0 || previousExam > 100) {
            previousExamEditText.setError("Exam score must be between 0 and 100");
            previousExamEditText.requestFocus();
            return;
        }
        if (learningActivity < 0 || learningActivity > 100) {
            learningActivityEditText.setError("Score must be between 0 and 100");
            learningActivityEditText.requestFocus();
            return;
        }

        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);
        int studentId = preferences.getInt("student_id", -1);

        if (studentId == -1) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setLoadingState(true);

        StudentData data = new StudentData(
                studentId,
                previousScore,
                studyHours,
                quizAverage,
                quizzesAttempted,
                previousExam,
                learningActivity
        );

        ApiClient.getApiService()
                .predict(data)
                .enqueue(new Callback<PredictionResponse>() {
                    @Override
                    public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                        setLoadingState(false);

                        if (response.isSuccessful() && response.body() != null) {
                            PredictionResponse result = response.body();
                            double predictedScore = result.getPredicted_score();

                            // Save current academic metrics & prediction
                            preferences.edit()
                                    .putFloat("previous_score", (float) previousScore)
                                    .putFloat("study_hours", (float) studyHours)
                                    .putFloat("quiz_average", (float) quizAverage)
                                    .putInt("quizzes_attempted", quizzesAttempted)
                                    .putFloat("previous_exam_score", (float) previousExam)
                                    .putFloat("learning_activity", (float) learningActivity)
                                    .putFloat("predicted_score", (float) predictedScore)
                                    .apply();

                            Intent intent = new Intent(AcademicDataActivity.this, PredictionActivity.class);
                            intent.putExtra("predicted_score", predictedScore);

                            if (result.getWeak_areas() != null) {
                                intent.putStringArrayListExtra("weak_areas", new ArrayList<>(result.getWeak_areas()));
                            }
                            if (result.getRecommendations() != null) {
                                intent.putStringArrayListExtra("recommendations", new ArrayList<>(result.getRecommendations()));
                            }
                            intent.putExtra("daily_goal", result.getDaily_goal());

                            startActivity(intent);
                            finish(); // Finish form so back button from prediction returns cleanly to dashboard
                        } else {
                            Toast.makeText(
                                    AcademicDataActivity.this,
                                    "Prediction request failed. Please try again.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PredictionResponse> call, Throwable t) {
                        setLoadingState(false);
                        Toast.makeText(
                                AcademicDataActivity.this,
                                "Unable to connect to the server. Please check your backend connection.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setLoadingState(boolean isLoading) {
        continueButton.setEnabled(!isLoading);
        if (academicProgressBar != null) {
            academicProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}