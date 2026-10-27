package com.example.edupredictai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface ApiService {

    @POST("predict")
    Call<PredictionResponse> predict(@Body StudentData data);

    @POST("register")
    Call<RegisterResponse> register(
            @Body RegisterRequest data
    );

    @POST("login")
    Call<LoginResponse> login(
            @Body LoginRequest data
    );

    @GET("academic/{student_id}")
    Call<AcademicResponse> getAcademicData(
            @Path("student_id") int studentId
    );

    @GET("academic/history/{student_id}")
    Call<HistoryResponse> getPredictionHistory(
            @Path("student_id") int studentId
    );
}