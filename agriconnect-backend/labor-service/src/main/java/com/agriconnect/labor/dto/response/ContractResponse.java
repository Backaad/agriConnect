package com.agriconnect.labor.dto.response;

import com.agriconnect.labor.domain.enums.ContractStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ContractResponse {
    private Long id;
    private Long applicationId;
    private boolean employerSigned;
    private boolean workerSigned;
    private ContractStatus status;
    private LocalDateTime createdAt;
}
