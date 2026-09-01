package auyesbay.dev.orderservice.domain;

import auyesbay.dev.api.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {
}
