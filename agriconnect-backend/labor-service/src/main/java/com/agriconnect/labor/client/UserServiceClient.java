package com.agriconnect.labor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${services.user.url:http://user-service:8082}")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    Object getPublicProfile(@PathVariable UUID userId);
}
