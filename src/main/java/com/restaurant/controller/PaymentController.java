package com.restaurant.controller;

import com.restaurant.dto.PaymentRequestDTO;
import com.restaurant.dto.PaymentResponseDTO;
import com.restaurant.service.PaymentService;
import com.restaurant.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TableService tableService;

    @PostMapping("/table/{tableId}")
    public ResponseEntity<PaymentResponseDTO> pay(
            @PathVariable Long tableId,
            @RequestHeader("X-Session-Token") String token,
            @Valid @RequestBody PaymentRequestDTO request) {

        tableService.validateToken(tableId, token);
        PaymentResponseDTO response = paymentService.processPayment(tableId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signature) {

        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentResponseDTO> confirmQRPayment(@PathVariable Long paymentId) {
        PaymentResponseDTO response = paymentService.confirmQRPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}
