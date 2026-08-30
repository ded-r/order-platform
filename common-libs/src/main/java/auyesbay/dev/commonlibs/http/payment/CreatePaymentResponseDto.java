package auyesbay.dev.commonlibs.http.payment;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long paymentId,
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        PaymentStatus paymentStatus
) {
}
