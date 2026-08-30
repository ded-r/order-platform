package auyesbay.dev.orderservice.domain;

import auyesbay.dev.commonlibs.http.order.CreateOrderRequestDto;
import auyesbay.dev.commonlibs.http.order.OrderStatus;
import auyesbay.dev.commonlibs.http.payment.CreatePaymentRequestDto;
import auyesbay.dev.commonlibs.http.payment.PaymentStatus;
import auyesbay.dev.orderservice.domain.db.OrderEntity;
import auyesbay.dev.orderservice.domain.db.OrderEntityMapper;
import auyesbay.dev.orderservice.domain.db.OrderEntityRepository;
import auyesbay.dev.orderservice.domain.db.OrderItemEntity;
import auyesbay.dev.orderservice.external.PaymentHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderProcessor {

    private final OrderEntityRepository orderEntityRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;


    public OrderEntity create(CreateOrderRequestDto createOrderRequestDto) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(createOrderRequestDto);
        calculatePricingForOrder(orderEntity);
        orderEntity.setOrderStatus(auyesbay.dev.commonlibs.http.order.OrderStatus.PENDING_PAYMENT);
        return orderEntityRepository.save(orderEntity);
    }

    public OrderEntity findOrder(Long id) {
        return orderEntityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    private void calculatePricingForOrder(OrderEntity orderEntity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity orderItemEntity : orderEntity.getItems()) {
            var randomPrice = ThreadLocalRandom.current().nextDouble(100, 5000);
            orderItemEntity.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));

            totalPrice = orderItemEntity.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(orderItemEntity.getQuantity()))
                    .add(totalPrice);
        }
        orderEntity.setTotalAmount(totalPrice);
    }

    public OrderEntity processPayment(
            Long id,
            OrderPaymentRequest request
    ) {
        var entity = findOrder(id);
        if (!entity.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new IllegalStateException("Order status must be PENDING_PAYMENT");
        }

        var response = paymentHttpClient.createPayment(CreatePaymentRequestDto.builder()
                .orderId(id)
                .paymentMethod(request.paymentMethod())
                .amount(entity.getTotalAmount())
                .build());

        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCEEDED) ?
                OrderStatus.PAYMENT_FAILED
                : OrderStatus.PAID;
        entity.setOrderStatus(status);

        return orderEntityRepository.save(entity);
    }
}

