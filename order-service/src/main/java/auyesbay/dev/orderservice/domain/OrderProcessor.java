package auyesbay.dev.orderservice.domain;

import auyesbay.dev.api.http.order.CreateOrderRequestDto;
import auyesbay.dev.api.http.order.OrderStatus;
import auyesbay.dev.api.http.payment.CreatePaymentRequestDto;
import auyesbay.dev.api.http.payment.CreatePaymentResponseDto;
import auyesbay.dev.api.http.payment.PaymentStatus;
import auyesbay.dev.api.kafka.DeliveryAssignedEvent;
import auyesbay.dev.api.kafka.OrderPaidEvent;
import auyesbay.dev.orderservice.domain.db.OrderEntity;
import auyesbay.dev.orderservice.domain.db.OrderEntityMapper;
import auyesbay.dev.orderservice.domain.db.OrderEntityRepository;
import auyesbay.dev.orderservice.domain.db.OrderItemEntity;
import auyesbay.dev.orderservice.external.PaymentHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProcessor {

    private final OrderEntityRepository orderEntityRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    public OrderEntity create(CreateOrderRequestDto createOrderRequestDto) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(createOrderRequestDto);
        calculatePricingForOrder(orderEntity);
        orderEntity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
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
                OrderStatus.PAID
                : OrderStatus.PAYMENT_FAILED;
        entity.setOrderStatus(status);

        var saved = orderEntityRepository.save(entity);

        if (status == OrderStatus.PAID) {
            sendOrderPaidEvent(saved, response);
        }

        return saved;
    }

    private void sendOrderPaidEvent(
            OrderEntity entity,
            CreatePaymentResponseDto response
    ) {
        kafkaTemplate.send(
                orderPaidTopic,
                entity.getId(),
                OrderPaidEvent.builder()
                        .orderId(entity.getId())
                        .amount(entity.getTotalAmount())
                        .paymentMethod(response.paymentMethod())
                        .paymentId(response.paymentId())
                        .build()
        ).thenAccept(result -> log.info("Order paid event sent with id = {}", entity.getId()));
    }

    public void processDeliveryAssigned(DeliveryAssignedEvent event) {

        var order = findOrder(event.orderId());

        if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
            processIncorrectDeliveryState(order);
            return;
        }

        order.setOrderStatus(OrderStatus.DELIVERY_ASSIGNED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        orderEntityRepository.save(order);
        log.info("Order delivery is processed. Order id = {}", order.getId());
    }

    private void processIncorrectDeliveryState(OrderEntity order) {
        if (order.getOrderStatus().equals(OrderStatus.DELIVERY_ASSIGNED)) {
            log.info("Order delivery already processed. Order id = {}", order.getId());
        } else if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
            log.error("Incorrect state of order when trying to process delivery. State = {}", order);
        }
    }
}

