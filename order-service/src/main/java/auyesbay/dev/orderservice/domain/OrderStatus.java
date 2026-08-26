package auyesbay.dev.orderservice.domain;

public enum OrderStatus {
    PAID,
    PENDING_PAYMENT,
    PAYMENT_FAILED,
    PENDING_DELIVERY,
    DELIVERED
}
