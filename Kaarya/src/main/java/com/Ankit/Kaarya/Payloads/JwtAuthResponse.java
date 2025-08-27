package com.Ankit.Kaarya.Payloads;

public class JwtAuthResponse {

    private String token;

    public JwtAuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}