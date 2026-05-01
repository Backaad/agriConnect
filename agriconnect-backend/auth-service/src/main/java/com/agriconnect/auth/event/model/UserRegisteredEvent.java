package com.agriconnect.auth.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private UUID userId;
    private String phone;
    private String role;
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();
    private String source;
}
