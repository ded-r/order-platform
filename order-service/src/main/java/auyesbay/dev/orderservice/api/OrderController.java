package auyesbay.dev.orderservice.api;

import auyesbay.dev.commonlibs.http.order.CreateOrderRequestDto;
import auyesbay.dev.commonlibs.http.order.OrderDto;
import auyesbay.dev.orderservice.domain.OrderPaymentRequest;
import auyesbay.dev.orderservice.domain.db.OrderEntity;
import auyesbay.dev.orderservice.domain.db.OrderEntityMapper;
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
    public OrderDto create(@RequestBody CreateOrderRequestDto request) {
        log.info("Creating order with request = {}", request);

        OrderEntity createdOrderEntity = orderProcessor.create(request);
        return orderEntityMapper.toOrderDto(createdOrderEntity);
    }
    @GetMapping("/{id}")
    public OrderDto findOrder(@PathVariable Long id) {
        log.info("Finding order with id = {}", id);

        OrderEntity foundOrderEntity = orderProcessor.findOrder(id);
        return orderEntityMapper.toOrderDto(foundOrderEntity);
    }

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(
            @PathVariable Long id,
            @RequestBody OrderPaymentRequest request) {
        log.info("Paying order with id = {}, request = {}", id, request);

        OrderEntity entity = orderProcessor.processPayment(id, request);
        return orderEntityMapper.toOrderDto(entity);
    }
}
