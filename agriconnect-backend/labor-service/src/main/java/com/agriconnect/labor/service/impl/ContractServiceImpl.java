package com.agriconnect.labor.service.impl;

import com.agriconnect.labor.domain.entity.Contract;
import com.agriconnect.labor.domain.enums.ContractStatus;
import com.agriconnect.labor.dto.request.SignContractRequest;
import com.agriconnect.labor.dto.response.ContractResponse;
import com.agriconnect.labor.repository.ContractRepository;
import com.agriconnect.labor.service.ContractService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    public ContractServiceImpl(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @Override
    public ContractResponse getContractByApplication(Long applicationId) {
        return mapToResponse(contractRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("Contract not found")));
    }

    @Override
    public ContractResponse signContract(SignContractRequest request, String userId, boolean isEmployer) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if (isEmployer) {
            if (!contract.getApplication().getMission().getEmployerId().equals(userId)) {
                throw new RuntimeException("Unauthorized");
            }
            contract.setEmployerSigned(true);
        } else {
            if (!contract.getApplication().getWorkerId().equals(userId)) {
                throw new RuntimeException("Unauthorized");
            }
            contract.setWorkerSigned(true);
        }

        if (contract.isEmployerSigned() && contract.isWorkerSigned()) {
            contract.setStatus(ContractStatus.SIGNED);
            contract.setPdfData(generateContractPdf(contract));
        }

        return mapToResponse(contractRepository.save(contract));
    }

    @Override
    public byte[] downloadContractPdf(Long contractId, String userId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if (!contract.getApplication().getMission().getEmployerId().equals(userId) &&
            !contract.getApplication().getWorkerId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (contract.getPdfData() == null) {
            throw new RuntimeException("PDF not yet generated (requires both signatures)");
        }

        return contract.getPdfData();
    }

    private byte[] generateContractPdf(Contract contract) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("AGRICONNECT LABOR CONTRACT").setBold().setFontSize(20));
            document.add(new Paragraph("Mission: " + contract.getApplication().getMission().getTitle()));
            document.add(new Paragraph("Employer ID: " + contract.getApplication().getMission().getEmployerId()));
            document.add(new Paragraph("Worker ID: " + contract.getApplication().getWorkerId()));
            document.add(new Paragraph("Salary: " + contract.getApplication().getMission().getSalary() + " EUR"));
            document.add(new Paragraph("Status: SIGNED"));
            document.add(new Paragraph("Date: " + java.time.LocalDate.now().toString()));

            document.add(new Paragraph("\nConditions: This digital contract serves as an agreement between the employer and the worker for the specified agricultural mission. Both parties have signed electronically."));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private ContractResponse mapToResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .applicationId(contract.getApplication().getId())
                .employerSigned(contract.isEmployerSigned())
                .workerSigned(contract.isWorkerSigned())
                .status(contract.getStatus())
                .createdAt(contract.getCreatedAt())
                .build();
    }
}
