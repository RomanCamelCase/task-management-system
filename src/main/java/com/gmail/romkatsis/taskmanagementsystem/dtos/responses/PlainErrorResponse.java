package com.gmail.romkatsis.taskmanagementsystem.dtos.responses;

public class PlainErrorResponse {

    private int statusCode;

    private String path;

    private String message;

    public PlainErrorResponse(int statusCode, String path, String message) {
        this.statusCode = statusCode;
        this.path = path;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
