package com.example.edupredictai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText;
    private RadioGroup optionsGroup;
    private RadioButton optionA;
    private RadioButton optionB;
    private RadioButton optionC;
    private RadioButton optionD;
    private Button submitButton;
    private TextView resultText;

    private TextView quizProgressText;
    private ProgressBar quizProgressBar;
    private View quizContentLayout;
    private View quizResultLayout;
    private TextView finalScoreText;
    private TextView quizResultMessage;
    private Button goToPredictionButton;
    private Button returnHomeButton;
    private ImageView backButton;

    private int currentQuestion = 0;
    private int score = 0;

    private final String[] questions = {
            "Which data structure follows LIFO?",
            "Which data structure follows FIFO?",
            "Which keyword is used to inherit a class in Java?",
            "Which algorithm is used to find the shortest path?"
    };

    private final String[][] options = {
            {"Queue", "Stack", "Array", "Linked List"},
            {"Stack", "Tree", "Queue", "Graph"},
            {"this", "extends", "super", "implements"},
            {"DFS", "BFS", "Dijkstra", "Binary Search"}
    };

    private final int[] correctAnswers = {
            1,
            2,
            1,
            2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);

        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);

        submitButton = findViewById(R.id.submitButton);
        resultText = findViewById(R.id.resultText);

        quizProgressText = findViewById(R.id.quizProgressText);
        quizProgressBar = findViewById(R.id.quizProgressBar);
        quizContentLayout = findViewById(R.id.quizContentLayout);
        quizResultLayout = findViewById(R.id.quizResultLayout);
        finalScoreText = findViewById(R.id.finalScoreText);
        quizResultMessage = findViewById(R.id.quizResultMessage);
        goToPredictionButton = findViewById(R.id.goToPredictionButton);
        returnHomeButton = findViewById(R.id.returnHomeButton);
        backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        if (goToPredictionButton != null) {
            goToPredictionButton.setOnClickListener(v -> {
                Intent intent = new Intent(QuizActivity.this, AcademicDataActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (returnHomeButton != null) {
            returnHomeButton.setOnClickListener(v -> finish());
        }

        showQuestion();

        submitButton.setOnClickListener(v -> checkAnswer());
    }

    private void showQuestion() {
        if (quizProgressText != null) {
            quizProgressText.setText("Question " + (currentQuestion + 1) + " of " + questions.length);
        }
        if (quizProgressBar != null) {
            quizProgressBar.setProgress(currentQuestion + 1);
        }

        questionText.setText(questions[currentQuestion]);

        optionA.setText(options[currentQuestion][0]);
        optionB.setText(options[currentQuestion][1]);
        optionC.setText(options[currentQuestion][2]);
        optionD.setText(options[currentQuestion][3]);

        optionsGroup.clearCheck();
        resultText.setText("");

        if (currentQuestion == questions.length - 1) {
            submitButton.setText("FINISH QUIZ");
        } else {
            submitButton.setText("NEXT QUESTION");
        }
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            resultText.setText("Please select an answer to continue.");
            return;
        }

        int selectedAnswer;
        if (selectedId == R.id.optionA) {
            selectedAnswer = 0;
        } else if (selectedId == R.id.optionB) {
            selectedAnswer = 1;
        } else if (selectedId == R.id.optionC) {
            selectedAnswer = 2;
        } else {
            selectedAnswer = 3;
        }

        if (selectedAnswer == correctAnswers[currentQuestion]) {
            score++;
        }

        if (currentQuestion < questions.length - 1) {
            currentQuestion++;
            showQuestion();
        } else {
            showFinalResult();
        }
    }

    private void showFinalResult() {
        double quizAverage = ((double) score / questions.length) * 100;

        if (quizContentLayout != null) {
            quizContentLayout.setVisibility(View.GONE);
        }
        if (quizResultLayout != null) {
            quizResultLayout.setVisibility(View.VISIBLE);
        }

        if (finalScoreText != null) {
            finalScoreText.setText(String.format(
                    Locale.getDefault(),
                    "%d/%d (%.0f%%)",
                    score,
                    questions.length,
                    quizAverage
            ));
        }

        if (quizResultMessage != null) {
            if (quizAverage >= 75) {
                quizResultMessage.setText("Outstanding work! You demonstrated strong foundational mastery. Your quiz average has been automatically synced to your profile.");
            } else if (quizAverage >= 50) {
                quizResultMessage.setText("Good attempt! Regular quiz practice will reinforce these core concepts and raise your predicted score.");
            } else {
                quizResultMessage.setText("Needs practice! Review the topics from this quiz and try again to improve your academic performance prediction.");
            }
        }

        // Save quiz result for prediction use
        getSharedPreferences("EduPredictData", MODE_PRIVATE)
                .edit()
                .putFloat("quiz_average", (float) quizAverage)
                .putInt("quizzes_attempted", questions.length)
                .apply();
    }
}