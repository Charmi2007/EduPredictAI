package com.example.edupredictai;

import java.util.List;

public class PredictionResponse {

    private double predicted_score;
    private List<String> weak_areas;
    private List<String> recommendations;
    private String daily_goal;

    public double getPredicted_score() {
        return predicted_score;
    }

    public List<String> getWeak_areas() {
        return weak_areas;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public String getDaily_goal() {
        return daily_goal;
    }
}