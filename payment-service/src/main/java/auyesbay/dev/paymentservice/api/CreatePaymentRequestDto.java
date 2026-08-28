package auyesbay.dev.paymentservice.api;

import auyesbay.dev.paymentservice.domain.PaymentMethod;
import auyesbay.dev.paymentservice.domain.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentRequestDto(
        Long orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}
