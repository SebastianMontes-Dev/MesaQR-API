package com.restaurant.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CreateTableRequest {
    private Integer tableNumber;
    private Integer capacity;
}
