package auyesbay.dev.orderservice.external;

import auyesbay.dev.commonlibs.http.payment.CreatePaymentRequestDto;import auyesbay.dev.commonlibs.http.payment.CreatePaymentResponseDto;import org.springframework.web.bind.annotation.PostMapping;import org.springframework.web.bind.annotation.RequestBody;import org.springframework.web.service.annotation.HttpExchange;import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/api/payments"
)
public interface PaymentHttpClient {

    @PostExchange
    CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto request);
}
