package auyesbay.dev.orderservice.api;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long itemId,
        String name,
        Integer quantity,
        BigDecimal priceAtPurchase
) {
}
