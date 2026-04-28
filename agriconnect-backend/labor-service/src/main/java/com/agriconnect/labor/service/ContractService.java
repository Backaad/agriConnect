package com.agriconnect.labor.service;

import com.agriconnect.labor.dto.request.SignContractRequest;
import com.agriconnect.labor.dto.response.ContractResponse;

public interface ContractService {
    ContractResponse getContractByApplication(Long applicationId);
    ContractResponse signContract(SignContractRequest request, String userId, boolean isEmployer);
    byte[] downloadContractPdf(Long contractId, String userId);
}
