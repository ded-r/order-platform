package auyesbay.dev.orderservice.domain.db;

import auyesbay.dev.api.http.order.CreateOrderRequestDto;
import auyesbay.dev.api.http.order.OrderDto;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {
    OrderEntity toEntity(CreateOrderRequestDto createOrderRequestDto);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }

    OrderDto toOrderDto(OrderEntity orderEntity);
}
