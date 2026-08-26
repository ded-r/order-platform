package auyesbay.dev.orderservice.api;

import auyesbay.dev.orderservice.domain.OrderEntity;
import auyesbay.dev.orderservice.domain.OrderEntityMapper;
import auyesbay.dev.orderservice.domain.OrderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public OrderDto create(@RequestBody CreateOrderRequestDto createOrderRequestDto) {
        log.info("Creating order with request = {}", createOrderRequestDto);
        OrderEntity createdOrderEntity = orderProcessor.create(createOrderRequestDto);
        return orderEntityMapper.toOrderDto(createdOrderEntity);
    }
    @GetMapping("/{id}")
    public OrderDto findOrder(@PathVariable Long id) {
        log.info("Finding order with id = {}", id);
        OrderEntity foundOrderEntity = orderProcessor.findOrder(id);
        return orderEntityMapper.toOrderDto(foundOrderEntity);
    }
}
