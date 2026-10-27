package com.example.edupredictai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameText;
    private TextView emailText;
    private TextView profilePredictionText;
    private TextView profileQuizText;
    private TextView profileStudyText;
    private TextView apiUrlText;
    private Button logoutButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        profilePredictionText = findViewById(R.id.profilePredictionText);
        profileQuizText = findViewById(R.id.profileQuizText);
        profileStudyText = findViewById(R.id.profileStudyText);
        apiUrlText = findViewById(R.id.apiUrlText);
        logoutButton = findViewById(R.id.logoutButton);
        backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        loadProfileData();

        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void loadProfileData() {
        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);

        String name = preferences.getString("student_name", "Student");
        String email = preferences.getString("student_email", "student@edupredict.ai");
        float predictedScore = preferences.getFloat("predicted_score", -1);
        float quizAverage = preferences.getFloat("quiz_average", -1);
        float studyHours = preferences.getFloat("study_hours", -1);

        nameText.setText(name);
        emailText.setText(email);

        if (predictedScore >= 0) {
            profilePredictionText.setText(String.format(Locale.getDefault(), "Predicted Score: %.1f%%", predictedScore));
        } else {
            profilePredictionText.setText("Predicted Score: Not Available");
        }

        if (quizAverage >= 0) {
            profileQuizText.setText(String.format(Locale.getDefault(), "Quiz Average: %.1f%%", quizAverage));
        } else {
            profileQuizText.setText("Quiz Average: Not Available");
        }

        if (studyHours >= 0) {
            profileStudyText.setText(String.format(Locale.getDefault(), "Study Hours: %.1f Hours / Day", studyHours));
        } else {
            profileStudyText.setText("Study Hours: Not Available");
        }

        if (apiUrlText != null) {
            apiUrlText.setText("API Endpoint: " + ApiClient.getBaseUrl());
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out of EduPredict AI?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        // Clear stored session data securely
        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}