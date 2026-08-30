package auyesbay.dev.commonlibs.http.order;

public record OrderItemRequestDto(
        Long itemId,
        String name,
        Integer quantity
) {
}
