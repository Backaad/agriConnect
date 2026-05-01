package com.agriconnect.commons.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtils {

    private MoneyUtils() {}

    public static String format(long amountFcfa) {
        return NumberFormat.getNumberInstance(Locale.FRENCH).format(amountFcfa) + " FCFA";
    }

    public static long applyPercentage(long amount, double percent) {
        return Math.round(amount * percent / 100.0);
    }

    public static long platformFee(long amount) {
        return applyPercentage(amount, 3.0);
    }
}
