package com.agriconnect.labor.dto.request;

import com.agriconnect.labor.domain.enums.PaymentMethod;
import com.agriconnect.labor.domain.enums.WorkType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateJobOfferRequest {

    @NotNull(message = "Le type de travail est obligatoire")
    private WorkType workType;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 20, max = 1000, message = "La description doit contenir entre 20 et 1000 caractères")
    private String description;

    @Min(value = 1, message = "Au moins 1 travailleur requis")
    @Max(value = 50, message = "Maximum 50 travailleurs par offre")
    @NotNull
    private Integer nbWorkers;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @NotNull(message = "Le salaire journalier est obligatoire")
    @Min(value = 500, message = "Salaire minimum 500 FCFA/jour")
    @Max(value = 500000, message = "Salaire maximum 500 000 FCFA/jour")
    private Long salaryFcfa;

    private PaymentMethod paymentMethod = PaymentMethod.ANY;

    private Boolean escrowEnabled = false;

    @NotNull(message = "La latitude est obligatoire")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitude;

    private String addressText;

    @Min(1) @Max(100)
    private Integer radiusKm = 10;

    private List<String> toolsProvided;
}
