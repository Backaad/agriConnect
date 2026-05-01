package com.agriconnect.labor.service;

import com.agriconnect.labor.dto.request.SignContractRequest;
import com.agriconnect.labor.dto.response.ContractResponse;

import java.util.UUID;

public interface ContractService {
    ContractResponse generateContract(UUID applicationId, UUID farmerId);
    ContractResponse getById(UUID id, UUID requesterId);
    ContractResponse sign(UUID contractId, UUID signerId, SignContractRequest request);
}
