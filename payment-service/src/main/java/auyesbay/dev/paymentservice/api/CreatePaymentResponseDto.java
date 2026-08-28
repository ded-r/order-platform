package auyesbay.dev.paymentservice.api;

import auyesbay.dev.paymentservice.domain.PaymentMethod;
import auyesbay.dev.paymentservice.domain.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long paymentId,
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        PaymentStatus paymentStatus
) {
}
