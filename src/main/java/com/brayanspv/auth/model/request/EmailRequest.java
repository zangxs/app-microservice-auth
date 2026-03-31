package com.brayanspv.auth.model.request;

public record EmailRequest(String from, String to, String subject, String body) {
}
