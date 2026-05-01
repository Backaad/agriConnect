package com.agriconnect.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsResponse {
    private String language;
    private boolean notifPush;
    private boolean notifSms;
    private boolean notifEmail;
    private boolean darkMode;
}
