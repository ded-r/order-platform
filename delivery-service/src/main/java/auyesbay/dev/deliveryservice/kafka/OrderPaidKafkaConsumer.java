package auyesbay.dev.deliveryservice.kafka;

import auyesbay.dev.api.kafka.OrderPaidEvent;
import auyesbay.dev.deliveryservice.domain.DeliveryProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@EnableKafka
@Configuration
@AllArgsConstructor
public class OrderPaidKafkaConsumer {

    private final DeliveryProcessor deliveryProcessor;

    @KafkaListener(
            topics = "${order-paid-topic}",
            containerFactory = "orderPaidEventListenerFactory"
    )
    public void listen(OrderPaidEvent event) {
        log.info("Received order paid even: {}", event);

        deliveryProcessor.processOrderPaid(event);
    }
}
