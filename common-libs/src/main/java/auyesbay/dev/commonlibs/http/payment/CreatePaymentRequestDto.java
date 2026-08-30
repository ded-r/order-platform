package auyesbay.dev.commonlibs.http.payment;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequestDto(
        Long orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}
