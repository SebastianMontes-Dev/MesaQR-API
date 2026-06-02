package com.restaurant.dto;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDTO {
    private Long id;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String notes;
}
