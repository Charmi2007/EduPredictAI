package com.example.edupredictai;

public class StudentData {

    private int student_id;
    private double previous_score;
    private double study_hours;
    private double quiz_average;
    private int quizzes_attempted;
    private double previous_exam_score;
    private double learning_activity;

    public StudentData(
            int student_id,
            double previous_score,
            double study_hours,
            double quiz_average,
            int quizzes_attempted,
            double previous_exam_score,
            double learning_activity) {

        this.student_id = student_id;
        this.previous_score = previous_score;
        this.study_hours = study_hours;
        this.quiz_average = quiz_average;
        this.quizzes_attempted = quizzes_attempted;
        this.previous_exam_score = previous_exam_score;
        this.learning_activity = learning_activity;
    }
}