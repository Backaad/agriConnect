package com.agriconnect.labor.controller;

import com.agriconnect.labor.dto.request.SignContractRequest;
import com.agriconnect.labor.dto.response.ContractResponse;
import com.agriconnect.labor.security.JwtTokenProvider;
import com.agriconnect.labor.service.ContractService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/labor/contracts")
public class ContractController {

    private final ContractService contractService;
    private final JwtTokenProvider tokenProvider;

    public ContractController(ContractService contractService, JwtTokenProvider tokenProvider) {
        this.contractService = contractService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ContractResponse> getContractByApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(contractService.getContractByApplication(applicationId));
    }

    @PostMapping("/sign/employer")
    public ResponseEntity<ContractResponse> signEmployer(@RequestBody SignContractRequest request,
                                                         @RequestHeader("Authorization") String token) {
        String userId = getUserId(token);
        return ResponseEntity.ok(contractService.signContract(request, userId, true));
    }

    @PostMapping("/sign/worker")
    public ResponseEntity<ContractResponse> signWorker(@RequestBody SignContractRequest request,
                                                       @RequestHeader("Authorization") String token) {
        String userId = getUserId(token);
        return ResponseEntity.ok(contractService.signContract(request, userId, false));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadContract(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String token) {
        String userId = getUserId(token);
        byte[] pdfBytes = contractService.downloadContractPdf(id, userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contract_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    private String getUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return tokenProvider.getUserIdFromJWT(authHeader.substring(7));
        }
        throw new RuntimeException("Invalid token");
    }
}
