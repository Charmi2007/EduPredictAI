package com.example.edupredictai;

import java.util.List;

public class HistoryResponse {

    private boolean success;
    private String message;
    private List<HistoryItem> history;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<HistoryItem> getHistory() {
        return history;
    }

    public static class HistoryItem {

        private int id;
        private double previous_score;
        private double study_hours;
        private double quiz_average;
        private int quizzes_attempted;
        private double previous_exam_score;
        private double learning_activity;
        private double predicted_score;
        private String created_at;

        public int getId() {
            return id;
        }

        public double getPrevious_score() {
            return previous_score;
        }

        public double getStudy_hours() {
            return study_hours;
        }

        public double getQuiz_average() {
            return quiz_average;
        }

        public int getQuizzes_attempted() {
            return quizzes_attempted;
        }

        public double getPrevious_exam_score() {
            return previous_exam_score;
        }

        public double getLearning_activity() {
            return learning_activity;
        }

        public double getPredicted_score() {
            return predicted_score;
        }

        public String getCreated_at() {
            return created_at;
        }
    }
}