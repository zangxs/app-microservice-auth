package com.brayanspv.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class SignUpRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "cant be blank")
    @NotNull
    private String username;
    @NotBlank
    @NotNull
    @Size(min = 8, max = 36)
    private String password;
    @Email
    @NotBlank
    @NotNull
    private String email;
}
