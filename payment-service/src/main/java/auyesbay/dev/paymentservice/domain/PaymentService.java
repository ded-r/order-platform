package auyesbay.dev.paymentservice.domain;

import auyesbay.dev.commonlibs.http.payment.CreatePaymentRequestDto;
import auyesbay.dev.commonlibs.http.payment.CreatePaymentResponseDto;
import auyesbay.dev.paymentservice.domain.db.PaymentEntity;
import auyesbay.dev.paymentservice.domain.db.PaymentEntityMapper;
import auyesbay.dev.paymentservice.domain.db.PaymentEntityRepository;
import auyesbay.dev.commonlibs.http.payment.PaymentMethod;
import auyesbay.dev.commonlibs.http.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentEntityMapper paymentEntityMapper;
    private final PaymentEntityRepository paymentEntityRepository;

    public CreatePaymentResponseDto createPayment(CreatePaymentRequestDto request) {
        var existingEntity = paymentEntityRepository.findByOrderId(request.orderId());

        if (existingEntity.isPresent()) {
            log.info("Payment already exists for order id: {}", request.orderId());
            return paymentEntityMapper.toResponseDto(existingEntity.get());
        }

        var entity = paymentEntityMapper.toEntity(request);

        var status = request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCEEDED;

        entity.setPaymentStatus(status);

        PaymentEntity saved = paymentEntityRepository.save(entity);

        return paymentEntityMapper.toResponseDto(saved);
    }
}
