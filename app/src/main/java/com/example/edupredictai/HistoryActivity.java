package com.example.edupredictai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;
    private View noHistoryLayout;
    private TextView noHistoryText;
    private ProgressBar historyProgressBar;
    private Button startPredictionButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.historyContainer);
        noHistoryLayout = findViewById(R.id.noHistoryLayout);
        noHistoryText = findViewById(R.id.noHistoryText);
        historyProgressBar = findViewById(R.id.historyProgressBar);
        startPredictionButton = findViewById(R.id.startPredictionButton);
        backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        if (startPredictionButton != null) {
            startPredictionButton.setOnClickListener(v -> {
                Intent intent = new Intent(HistoryActivity.this, AcademicDataActivity.class);
                startActivity(intent);
                finish();
            });
        }

        loadPredictionHistory();
    }

    private void loadPredictionHistory() {
        SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);
        int studentId = preferences.getInt("student_id", -1);

        if (studentId == -1) {
            Toast.makeText(this, "Please login again.", Toast.LENGTH_SHORT).show();
            showEmptyState(true);
            return;
        }

        if (historyProgressBar != null) {
            historyProgressBar.setVisibility(View.VISIBLE);
        }

        ApiClient.getApiService()
                .getPredictionHistory(studentId)
                .enqueue(new Callback<HistoryResponse>() {
                    @Override
                    public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                        if (historyProgressBar != null) {
                            historyProgressBar.setVisibility(View.GONE);
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            HistoryResponse result = response.body();

                            if (result.isSuccess() && result.getHistory() != null && !result.getHistory().isEmpty()) {
                                showEmptyState(false);
                                displayHistory(result.getHistory());
                            } else {
                                showEmptyState(true);
                            }
                        } else {
                            showEmptyState(true);
                            Toast.makeText(
                                    HistoryActivity.this,
                                    "Could not retrieve history from server.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<HistoryResponse> call, Throwable t) {
                        if (historyProgressBar != null) {
                            historyProgressBar.setVisibility(View.GONE);
                        }
                        showEmptyState(true);
                        Toast.makeText(
                                HistoryActivity.this,
                                "Unable to connect to the server. Please check your connection.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void showEmptyState(boolean isEmpty) {
        if (noHistoryLayout != null) {
            noHistoryLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (noHistoryText != null) {
            noHistoryText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (historyContainer != null && isEmpty) {
            historyContainer.removeAllViews();
        }
    }

    private void displayHistory(List<HistoryResponse.HistoryItem> history) {
        historyContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (HistoryResponse.HistoryItem item : history) {
            View card = inflater.inflate(R.layout.item_history_card, historyContainer, false);

            TextView cardDateText = card.findViewById(R.id.cardDateText);
            TextView cardPredictedScoreText = card.findViewById(R.id.cardPredictedScoreText);
            TextView cardPreviousScoreText = card.findViewById(R.id.cardPreviousScoreText);
            TextView cardQuizAverageText = card.findViewById(R.id.cardQuizAverageText);
            TextView cardStudyHoursText = card.findViewById(R.id.cardStudyHoursText);

            cardDateText.setText(formatDate(item.getCreated_at()));
            cardPredictedScoreText.setText(String.format(Locale.getDefault(), "Predicted: %.1f%%", item.getPredicted_score()));
            cardPreviousScoreText.setText(String.format(Locale.getDefault(), "%.1f%%", item.getPrevious_score()));
            cardQuizAverageText.setText(String.format(Locale.getDefault(), "%.1f%%", item.getQuiz_average()));
            cardStudyHoursText.setText(String.format(Locale.getDefault(), "%.1f hrs", item.getStudy_hours()));

            historyContainer.addView(card);
        }
    }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return "Date Unavailable";
        }
        // Normalize ISO format (e.g., 2026-10-12T19:09:36 -> 2026-10-12 19:09)
        String formatted = date.replace("T", " ");
        if (formatted.length() > 16) {
            formatted = formatted.substring(0, 16);
        }
        return formatted;
    }
}