package com.brayanspv.auth.repositories.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("password_reset_token")
public class PasswordResetToken {
    @Id
    private UUID id;
    @Column("user_id")
    private Long userId;
    private String code;
    private boolean used;
    @Column("expires_at")
    private LocalDateTime expiresAt;
    @Column("created_at")
    private LocalDateTime createdAt;

}
