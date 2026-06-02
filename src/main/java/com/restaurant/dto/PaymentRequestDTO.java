package com.restaurant.dto;

import com.restaurant.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    @NotNull
    private PaymentMethod method;
    private String paymentProviderToken;
}
