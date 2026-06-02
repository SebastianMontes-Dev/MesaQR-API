package com.restaurant.dto;

import com.restaurant.model.OrderStatus;
import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderSummaryDTO {
    private Long orderId;
    private Integer tableNumber;
    private List<OrderItemDTO> items;
    private BigDecimal total;
    private OrderStatus status;
}
