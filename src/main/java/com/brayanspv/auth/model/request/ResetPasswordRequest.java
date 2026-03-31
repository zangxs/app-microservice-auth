package com.brayanspv.auth.model.request;

public record ResetPasswordRequest(String email, String code,String password) {
}
