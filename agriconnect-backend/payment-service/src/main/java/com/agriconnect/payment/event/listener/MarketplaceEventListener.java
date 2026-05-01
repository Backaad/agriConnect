package com.agriconnect.payment.event.listener;

import com.agriconnect.payment.dto.request.EscrowLockRequest;
import com.agriconnect.payment.event.model.OrderConfirmedEvent;
import com.agriconnect.payment.service.EscrowService;
import com.agriconnect.payment.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketplaceEventListener {

    private final EscrowService escrowService;
    private final WalletService walletService;

    @KafkaListener(topics = "marketplace.order.confirmed", groupId = "payment-service")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        log.info("OrderConfirmed reçu: orderId={} amount={} FCFA", event.getOrderId(), event.getAmountFcfa());
        try {
            walletService.ensureWalletExists(event.getConsumerId());
            walletService.ensureWalletExists(event.getFarmerId());

            EscrowLockRequest lockReq = new EscrowLockRequest();
            lockReq.setReferenceId(event.getOrderId());
            lockReq.setReferenceType("MARKETPLACE_ORDER");
            lockReq.setPayerId(event.getConsumerId());
            lockReq.setPayeeId(event.getFarmerId());
            lockReq.setAmountFcfa(event.getAmountFcfa());

            escrowService.lock(lockReq);
        } catch (Exception e) {
            log.error("Erreur lock escrow pour order {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}
