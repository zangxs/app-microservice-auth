package com.brayanspv.auth.model.request;

public record VerifyCodeRequest(String email, String code) {
}
