package com.example.edupredictai;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private ProgressBar registerProgressBar;
    private TextView loginLinkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ApiClient.init(this);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        registerProgressBar = findViewById(R.id.registerProgressBar);
        loginLinkText = findViewById(R.id.loginLinkText);

        ImageView serverSettingsButton = findViewById(R.id.serverSettingsButton);
        if (serverSettingsButton != null) {
            serverSettingsButton.setOnClickListener(v -> ApiClient.showServerConfigDialog(this, null));
        }

        registerButton.setOnClickListener(v -> registerStudent());

        if (loginLinkText != null) {
            loginLinkText.setOnClickListener(v -> finish());
        }
    }

    private void registerStudent() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || name.length() < 2) {
            nameEditText.setError("Please enter your full name");
            nameEditText.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        setLoadingState(true);

        RegisterRequest request = new RegisterRequest(name, email, password);

        ApiClient.getApiService()
                .register(request)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        setLoadingState(false);

                        if (response.isSuccessful() && response.body() != null) {
                            RegisterResponse result = response.body();

                            if (result.isSuccess()) {
                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Account created successfully! Please sign in.",
                                        Toast.LENGTH_LONG
                                ).show();
                                finish();
                            } else {
                                Toast.makeText(
                                        RegisterActivity.this,
                                        result.getMessage() != null ? result.getMessage() : "Registration failed.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } else {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration failed. Email may already be in use.",
                                    Toast.LENGTH_LONG
                                ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        setLoadingState(false);
                        String currentUrl = ApiClient.getBaseUrl(RegisterActivity.this);
                        Toast.makeText(
                                RegisterActivity.this,
                                "Unable to connect to server (" + currentUrl + "). Tap the top-right gear icon to configure your IP address.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setLoadingState(boolean isLoading) {
        registerButton.setEnabled(!isLoading);
        if (registerProgressBar != null) {
            registerProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}