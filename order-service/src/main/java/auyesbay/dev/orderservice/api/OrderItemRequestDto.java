package auyesbay.dev.orderservice.api;

public record OrderItemRequestDto(
        Long itemId,
        String name,
        Integer quantity
) {
}
