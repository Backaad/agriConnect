package com.agriconnect.payment.domain.enums;
public enum PaymentProvider {
    MTN_MOMO,     // MTN Mobile Money
    ORANGE_MONEY, // Orange Money
    CAMPAY,       // Agrégateur Campay (fallback)
    WALLET        // Paiement depuis le wallet interne
}
