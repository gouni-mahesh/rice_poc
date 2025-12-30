package com.neo.tech.whatsappPoc.util;

import com.neo.tech.whatsappPoc.order.entity.OrderEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
@Getter
public class ContactCreatedEvent extends ApplicationEvent {
    private final List<String> branchIds ;
    private final OrderEntity orderEntity;

    public ContactCreatedEvent(Object source, List<String> branchIds, OrderEntity orderEntity) {
        super(source);
        this.branchIds = branchIds;
        this.orderEntity = orderEntity;
    }
}
