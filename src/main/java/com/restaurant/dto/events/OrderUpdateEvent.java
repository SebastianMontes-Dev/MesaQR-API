package com.restaurant.dto.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderUpdateEvent extends TableEvent {
    private BigDecimal total;
    private int itemCount;
    private Long orderId;

    public OrderUpdateEvent() {
        super();
    }

    public OrderUpdateEvent(Long tableId, Integer tableNumber, Long orderId, BigDecimal total, int itemCount) {
        super(tableId, tableNumber);
        this.orderId = orderId;
        this.total = total;
        this.itemCount = itemCount;
    }
}
