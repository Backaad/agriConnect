package com.agriconnect.user.dto.request;

import lombok.Data;

@Data
public class UpdateSettingsRequest {
    private String language;
    private Boolean notifPush;
    private Boolean notifSms;
    private Boolean notifEmail;
    private Boolean darkMode;
}
