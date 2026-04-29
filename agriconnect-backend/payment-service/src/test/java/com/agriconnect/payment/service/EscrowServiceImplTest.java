package com.agriconnect.payment.service;

import com.agriconnect.payment.domain.entity.Escrow;
import com.agriconnect.payment.domain.entity.Wallet;
import com.agriconnect.payment.domain.enums.EscrowStatus;
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

    @Mock
    private TaraApiService taraApiService;

    @InjectMocks
    private EscrowServiceImpl escrowService;

    @BeforeEach
    void setUp() {
        // Setup initial if needed
    }

    @Test
    void testInitiateEscrow() {
        // Given
        Long missionId = 1L;
        Long farmerId = 10L;
        Long workerId = 20L;
        BigDecimal amount = new BigDecimal("1000.00");
        
        when(taraApiService.initiatePaymentRequest(anyString(), any(BigDecimal.class)))
                .thenReturn("TARA-TX-123");
        
        when(escrowRepository.save(any(Escrow.class))).thenAnswer(invocation -> {
            Escrow e = invocation.getArgument(0);
            e.setId(100L);
            return e;
        });

        // When
        Escrow result = escrowService.initiateEscrow(missionId, farmerId, workerId, amount);

        // Then
        assertNotNull(result);
        assertEquals(EscrowStatus.PENDING_PAYMENT, result.getStatus());
        assertEquals("TARA-TX-123", result.getTaraTransactionId());
        assertEquals(new BigDecimal("40.00"), result.getCommission()); // 4% de 1000
        assertEquals(new BigDecimal("960.00"), result.getWorkerAmount());
        
        verify(taraApiService).initiatePaymentRequest(anyString(), eq(amount));
        verify(escrowRepository).save(any(Escrow.class));
    }

    @Test
    void testReleaseEscrow() {
        // Given
        Long escrowId = 100L;
        Escrow escrow = Escrow.builder()
                .id(escrowId)
                .missionId(1L)
                .farmerId(10L)
                .workerId(20L)
                .amount(new BigDecimal("1000.00"))
                .commission(new BigDecimal("40.00"))
                .workerAmount(new BigDecimal("960.00"))
                .status(EscrowStatus.HELD)
                .build();
                
        Wallet workerWallet = Wallet.builder()
                .userId(20L)
                .mobileMoneyNumber("237699999999")
                .balance(BigDecimal.ZERO)
                .build();

        when(escrowRepository.findById(escrowId)).thenReturn(Optional.of(escrow));
        when(walletRepository.findByUserId(20L)).thenReturn(Optional.of(workerWallet));
        when(taraApiService.transferToMobileMoney(anyString(), any(BigDecimal.class))).thenReturn(true);
        when(escrowRepository.save(any(Escrow.class))).thenReturn(escrow);

        // When
        Escrow result = escrowService.releaseEscrow(escrowId);

        // Then
        assertNotNull(result);
        assertEquals(EscrowStatus.RELEASED, result.getStatus());
        verify(taraApiService).transferToMobileMoney("237699999999", new BigDecimal("960.00"));
        verify(transactionRepository, times(2)).save(any()); // Worker receiving + Platform commission
    }
}
