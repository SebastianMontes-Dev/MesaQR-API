package com.restaurant.dto;

import com.restaurant.model.PaymentStatus;
import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponseDTO {
    private Long paymentId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String message;
    private String clientSecret;
    private String redirectUrl;
}
