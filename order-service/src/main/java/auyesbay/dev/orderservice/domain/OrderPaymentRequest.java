package auyesbay.dev.orderservice.domain;

import auyesbay.dev.commonlibs.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {
}
