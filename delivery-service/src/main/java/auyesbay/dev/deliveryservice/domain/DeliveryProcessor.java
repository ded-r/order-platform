package auyesbay.dev.deliveryservice.domain;

import auyesbay.dev.api.kafka.DeliveryAssignedEvent;
import auyesbay.dev.api.kafka.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryProcessor {

    private final DeliveryEntityRepository deliveryEntityRepository;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    @Value("${delivery-assigned-topic}")
    private String deliveryAssignedTopic;

    public void processOrderPaid(OrderPaidEvent event) {

        var orderId = event.orderId();

        var found = deliveryEntityRepository.findByOrderId(orderId);

        if (found.isPresent()) {
            log.info("Found delivery was already assigned. Delivery = {}", found.get());
            return;
        }

        var assignedDelivery = assignDelivery(event, orderId);
        sendDeliveryAssignedEvent(assignedDelivery);
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {

        kafkaTemplate.send(
                deliveryAssignedTopic,
                assignedDelivery.getOrderId(),
                DeliveryAssignedEvent.builder()
                        .courierName(assignedDelivery.getCourierName())
                        .orderId(assignedDelivery.getOrderId())
                        .etaMinutes(assignedDelivery.getEtaMinutes())
                        .build()
        ).thenAccept(result -> {
            log.info("Delivery assigned event sent. Delivery id = {}", assignedDelivery.getId());
        });
    }

    private DeliveryEntity assignDelivery(OrderPaidEvent event, Long orderId) {

        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierName("courier" + ThreadLocalRandom.current().nextInt(100));
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(15, 45));

        log.info("Delivery was saved successfully. Delivery = {}", event);
        return deliveryEntityRepository.save(entity);
    }
}
