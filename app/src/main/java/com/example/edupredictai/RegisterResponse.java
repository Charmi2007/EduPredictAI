package com.example.edupredictai;

public class RegisterResponse {

    private boolean success;
    private String message;
    private int student_id;
    private String name;
    private String email;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getStudent_id() {
        return student_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}