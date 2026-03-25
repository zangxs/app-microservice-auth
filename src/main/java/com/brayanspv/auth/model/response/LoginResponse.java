package com.brayanspv.auth.model.response;

import lombok.Data;

import java.io.Serializable;
@Data
public class LoginResponse implements Serializable {
    private String jwtToken;
}
