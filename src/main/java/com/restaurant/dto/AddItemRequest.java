package com.restaurant.dto;

import lombok.Data;

@Data
public class AddItemRequest {
    private Long menuItemId;
    private Integer quantity = 1;
    private String notes;
}
