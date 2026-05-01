package com.agriconnect.payment.service;

import com.agriconnect.payment.domain.entity.Escrow;
import com.agriconnect.payment.domain.entity.Wallet;
import com.agriconnect.payment.domain.enums.EscrowStatus;
import com.agriconnect.payment.dto.request.EscrowLockRequest;
import com.agriconnect.payment.dto.request.EscrowReleaseRequest;
import com.agriconnect.payment.dto.response.EscrowResponse;
import com.agriconnect.payment.repository.EscrowRepository;
import com.agriconnect.payment.repository.TransactionRepository;
import com.agriconnect.payment.repository.WalletRepository;
import com.agriconnect.payment.service.impl.EscrowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscrowServiceImplTest {

    @Mock
    private EscrowRepository escrowRepository;
    
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private EscrowServiceImpl escrowService;

    private UUID payerId;
    private UUID payeeId;
    private UUID referenceId;

    @BeforeEach
    void setUp() {
        payerId = UUID.randomUUID();
        payeeId = UUID.randomUUID();
        referenceId = UUID.randomUUID();
    }

    @Test
    void testLockEscrow_Success() {
        // Given
        BigDecimal amount = new BigDecimal("10000");
        EscrowLockRequest request = EscrowLockRequest.builder()
                .referenceId(referenceId)
                .referenceType("JOB_REQUEST")
                .payerId(payerId)
                .payeeId(payeeId)
                .amountFcfa(amount.longValue())
                .build();
        
        Wallet payerWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(payerId)
                .balanceFcfa(20000L)
                .frozenFcfa(0L)
                .build();

        when(walletRepository.findByUserIdForUpdate(payerId)).thenReturn(Optional.of(payerWallet));
        when(escrowRepository.existsByReferenceIdAndStatus(referenceId, EscrowStatus.LOCKED)).thenReturn(false);
        when(escrowRepository.save(any(Escrow.class))).thenAnswer(invocation -> {
            Escrow e = invocation.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        // When
        EscrowResponse result = escrowService.lock(request);

        // Then
        assertNotNull(result);
        assertEquals(EscrowStatus.LOCKED, result.getStatus());
        assertEquals(amount.longValue(), result.getAmountFcfa());
        
        verify(walletRepository).save(any(Wallet.class));
        verify(escrowRepository).save(any(Escrow.class));
    }

    @Test
    void testLockEscrow_InsufficientFunds() {
        // Given
        BigDecimal amount = new BigDecimal("10000");
        EscrowLockRequest request = EscrowLockRequest.builder()
                .referenceId(referenceId)
                .referenceType("JOB_REQUEST")
                .payerId(payerId)
                .payeeId(payeeId)
                .amountFcfa(amount.longValue())
                .build();
        
        Wallet payerWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(payerId)
                .balanceFcfa(5000L)
                .frozenFcfa(0L)
                .build();

        when(walletRepository.findByUserIdForUpdate(payerId)).thenReturn(Optional.of(payerWallet));
        when(escrowRepository.existsByReferenceIdAndStatus(referenceId, EscrowStatus.LOCKED)).thenReturn(false);

        // When & Then
        assertThrows(Exception.class, () -> escrowService.lock(request));
    }

    @Test
    void testReleaseEscrow_Success() {
        // Given
        UUID escrowId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(10000);
        long fee = 300; // 3% of 10000
        
        Escrow escrow = Escrow.builder()
                .id(escrowId)
                .referenceId(referenceId)
                .referenceType("JOB_REQUEST")
                .payerId(payerId)
                .payeeId(payeeId)
                .amountFcfa(amount.longValue())
                .platformFee(fee)
                .status(EscrowStatus.LOCKED)
                .build();
                
        Wallet payeeWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(payeeId)
                .balanceFcfa(0L)
                .frozenFcfa(0L)
                .build();

        EscrowReleaseRequest request = EscrowReleaseRequest.builder()
                .referenceId(escrowId)
                .build();

        when(escrowRepository.findById(escrowId)).thenReturn(Optional.of(escrow));
        when(walletRepository.findByUserIdForUpdate(payeeId)).thenReturn(Optional.of(payeeWallet));
        when(escrowRepository.save(any(Escrow.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        EscrowResponse result = escrowService.release(request);

        // Then
        assertNotNull(result);
        assertEquals(EscrowStatus.RELEASED, result.getStatus());
        
        verify(walletRepository).save(any(Wallet.class));
        verify(escrowRepository, times(2)).save(any(Escrow.class));
        verify(transactionRepository, times(2)).save(any());
    }
}
