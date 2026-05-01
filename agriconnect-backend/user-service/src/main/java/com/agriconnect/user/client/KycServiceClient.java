package com.agriconnect.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "kyc-service", url = "${services.kyc.url:http://kyc-service:8083}")
public interface KycServiceClient {

    @GetMapping("/api/v1/kyc/{userId}/status")
    String getKycStatus(@PathVariable UUID userId);
}
