package auyesbay.dev.orderservice.external;

import auyesbay.dev.api.http.payment.CreatePaymentRequestDto;
import auyesbay.dev.api.http.payment.CreatePaymentResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/api/payments"
)
public interface PaymentHttpClient {

    @PostExchange
    CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto request);
}
