package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class E {
    public static BigDecimal valE(int iterations) {
        BigDecimal e = BigDecimal.ONE;
        BigDecimal factorial = BigDecimal.ONE;

        for (int i = 1; i <= iterations; i++) {
            factorial = factorial.multiply(BigDecimal.valueOf(i));
            e = e.add(BigDecimal.ONE.divide(factorial, 25, RoundingMode.HALF_UP));
        }
        return e;
    }
}
