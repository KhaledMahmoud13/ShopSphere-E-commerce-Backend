package com.khaled.shopsphere.order.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    @EventListener
    public void handle(OrderCreatedEvent event) {
        log.info(
                "New order created. orderId={}, userId={}",
                event.orderId(),
                event.userId()
        );
    }
}
