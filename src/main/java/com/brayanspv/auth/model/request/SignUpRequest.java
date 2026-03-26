package com.brayanspv.auth.model.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class SignUpRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String email;
}
