package auyesbay.dev.orderservice.api;

import auyesbay.dev.orderservice.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

public record OrderDto(
        Long id,
        Long customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemDto> items
) {
}
