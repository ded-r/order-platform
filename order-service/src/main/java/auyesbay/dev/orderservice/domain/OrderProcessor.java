package auyesbay.dev.orderservice.domain;

import auyesbay.dev.orderservice.api.CreateOrderRequestDto;
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
}

