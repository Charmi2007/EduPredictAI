package com.example.edupredictai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerText;
    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ApiClient.init(this);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        ImageView serverSettingsButton = findViewById(R.id.serverSettingsButton);
        if (serverSettingsButton != null) {
            serverSettingsButton.setOnClickListener(v -> ApiClient.showServerConfigDialog(this, null));
        }

        loginButton.setOnClickListener(v -> loginStudent());

        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginStudent() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Please enter your email");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Please enter your password");
            passwordEditText.requestFocus();
            return;
        }

        setLoadingState(true);

        LoginRequest request = new LoginRequest(email, password);

        ApiClient.getApiService()
                .login(request)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        setLoadingState(false);

                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse result = response.body();

                            if (result.isSuccess()) {
                                // Save logged-in student information
                                SharedPreferences preferences = getSharedPreferences(
                                        "EduPredictData",
                                        MODE_PRIVATE
                                );

                                preferences.edit()
                                        .putInt("student_id", result.getStudent_id())
                                        .putString("student_name", result.getName())
                                        .putString("student_email", result.getEmail())
                                        .apply();

                                Toast.makeText(
                                        LoginActivity.this,
                                        "Welcome back, " + result.getName() + "!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(
                                        LoginActivity.this,
                                        result.getMessage() != null ? result.getMessage() : "Invalid email or password.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } else {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login failed. Please check your credentials.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        setLoadingState(false);
                        String currentUrl = ApiClient.getBaseUrl(LoginActivity.this);
                        Toast.makeText(
                                LoginActivity.this,
                                "Unable to connect to server (" + currentUrl + "). Tap the top-right gear icon to configure your IP address.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setLoadingState(boolean isLoading) {
        loginButton.setEnabled(!isLoading);
        if (loginProgressBar != null) {
            loginProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}