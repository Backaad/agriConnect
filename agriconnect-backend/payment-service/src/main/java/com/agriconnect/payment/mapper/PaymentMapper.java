package com.agriconnect.payment.mapper;

import com.agriconnect.payment.domain.entity.*;
import com.agriconnect.payment.dto.response.*;
import com.agriconnect.commons.util.MoneyUtils;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "availableBalanceFcfa",
             expression = "java(wallet.getAvailableBalance())")
    @Mapping(target = "formattedBalance",
             expression = "java(com.agriconnect.commons.util.MoneyUtils.format(wallet.getBalanceFcfa()))")
    @Mapping(target = "formattedAvailable",
             expression = "java(com.agriconnect.commons.util.MoneyUtils.format(wallet.getAvailableBalance()))")
    WalletResponse toWalletResponse(Wallet wallet);

    @Mapping(target = "typeLabel",   expression = "java(tx.getType().name())")
    @Mapping(target = "statusLabel", expression = "java(tx.getStatus().name())")
    TransactionResponse toTransactionResponse(Transaction tx);

    @Mapping(target = "netPayeeAmount",
             expression = "java(escrow.getNetPayeeAmount())")
    EscrowResponse toEscrowResponse(Escrow escrow);

    @Mapping(target = "maskedMobileNumber",
             expression = "java(com.agriconnect.commons.util.PhoneUtils.mask(w.getMobileNumber()))")
    WithdrawalResponse toWithdrawalResponse(WithdrawalRequest w);
}
