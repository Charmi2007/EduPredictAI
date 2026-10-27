package com.example.edupredictai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiClient.init(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences preferences = getSharedPreferences("EduPredictData", MODE_PRIVATE);
            int studentId = preferences.getInt("student_id", -1);

            Intent intent;
            if (studentId != -1) {
                // User is already logged in, navigate directly to Home Dashboard
                intent = new Intent(MainActivity.this, HomeActivity.class);
            } else {
                // Not logged in, navigate to Login
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}