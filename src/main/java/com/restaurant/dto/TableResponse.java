package com.restaurant.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TableResponse {
    private Long id;
    private Integer tableNumber;
    private Integer capacity;
    private String status;
    private String qrUrl;
}
