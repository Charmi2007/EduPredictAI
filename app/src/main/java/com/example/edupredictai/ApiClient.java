package com.example.edupredictai;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    /**
     * Primary Wi-Fi IP of the host development computer for physical phones.
     * Fallback emulator address: "http://10.0.2.2:8000/"
     */
    public static final String DEFAULT_BASE_URL = "http://10.41.134.71:8000/";    public static final String EMULATOR_BASE_URL = "http://10.0.2.2:8000/";

    private static String currentBaseUrl = null;
    private static ApiService apiService = null;

    public static synchronized void init(Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("EduPredictData", Context.MODE_PRIVATE);
            currentBaseUrl = prefs.getString("server_base_url", DEFAULT_BASE_URL);
        }
    }

    public static synchronized String getBaseUrl(Context context) {
        if (currentBaseUrl == null && context != null) {
            init(context);
        }
        if (currentBaseUrl == null) {
            currentBaseUrl = DEFAULT_BASE_URL;
        }
        return currentBaseUrl;
    }

    public static String getBaseUrl() {
        if (currentBaseUrl == null) {
            currentBaseUrl = DEFAULT_BASE_URL;
        }
        return currentBaseUrl;
    }

    public static synchronized void setBaseUrl(Context context, String newUrl) {
        if (newUrl != null && !newUrl.trim().isEmpty()) {
            newUrl = newUrl.trim();
            if (!newUrl.endsWith("/")) {
                newUrl += "/";
            }
            currentBaseUrl = newUrl;
            apiService = null; // Recreate Retrofit instance

            if (context != null) {
                SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("EduPredictData", Context.MODE_PRIVATE);
                prefs.edit().putString("server_base_url", newUrl).apply();
            }
        }
    }

    public static synchronized void setBaseUrl(String newUrl) {
        setBaseUrl(null, newUrl);
    }

    public static synchronized ApiService getApiService() {
        if (apiService == null) {
            String baseUrl = getBaseUrl();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public static void showServerConfigDialog(Context context, Runnable onSaved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Server Connection Settings");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_server_config, null);
        EditText urlInput = view.findViewById(R.id.serverUrlEditText);
        Button btnWifi = view.findViewById(R.id.btnPresetWifi);
        Button btnEmulator = view.findViewById(R.id.btnPresetEmulator);

        String current = getBaseUrl(context);
        urlInput.setText(current);

        btnWifi.setOnClickListener(v -> urlInput.setText(DEFAULT_BASE_URL));
        btnEmulator.setOnClickListener(v -> urlInput.setText(EMULATOR_BASE_URL));

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String enteredUrl = urlInput.getText().toString().trim();
            if (!enteredUrl.isEmpty()) {
                setBaseUrl(context, enteredUrl);
                Toast.makeText(context, "Server URL set to: " + getBaseUrl(context), Toast.LENGTH_SHORT).show();
                if (onSaved != null) {
                    onSaved.run();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}