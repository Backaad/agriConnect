package com.agriconnect.payment.service;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.payment.dto.request.WithdrawRequest;
import com.agriconnect.payment.dto.response.WithdrawalResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WithdrawalService {
    WithdrawalResponse requestWithdrawal(UUID userId, WithdrawRequest request);
    PageResponse<WithdrawalResponse> getHistory(UUID userId, Pageable pageable);
}
