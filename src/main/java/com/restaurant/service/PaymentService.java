package com.restaurant.service;

import com.restaurant.dto.PaymentRequestDTO;
import com.restaurant.dto.PaymentResponseDTO;
import com.restaurant.model.Order;
import com.restaurant.model.Payment;
import com.restaurant.model.PaymentMethod;
import com.restaurant.model.PaymentStatus;
import com.restaurant.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public PaymentResponseDTO processPayment(Long tableId, PaymentRequestDTO request) {
        Order order = orderService.getActiveOrder(tableId);
        BigDecimal total = orderService.getOrderTotal(order.getId());

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(request.getMethod());
        payment.setAmount(total);

        return switch (request.getMethod()) {
            case CASH -> processCashPayment(payment, order);
            case CARD -> processCardPayment(payment, order, request.getPaymentProviderToken());
            case QR_TRANSFER -> processQRTransferPayment(payment, order);
        };
    }

    private PaymentResponseDTO processCashPayment(Payment payment, Order order) {
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        orderService.markAsPaid(order.getTable().getId());

        log.info("Pago en efectivo completado: mesa {} - ${}",
                order.getTable().getTableNumber(), payment.getAmount());

        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(PaymentStatus.COMPLETED)
                .amount(payment.getAmount())
                .message("Pago en efectivo registrado")
                .build();
    }

    private PaymentResponseDTO processCardPayment(Payment payment, Order order, String providerToken) {
        payment.setProviderRef(providerToken);
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        orderService.markAsPaid(order.getTable().getId());

        log.info("Pago con tarjeta procesado: mesa {} - ${}",
                order.getTable().getTableNumber(), payment.getAmount());

        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(PaymentStatus.COMPLETED)
                .amount(payment.getAmount())
                .message("Pago con tarjeta procesado")
                .build();
    }

    private PaymentResponseDTO processQRTransferPayment(Payment payment, Order order) {
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        BigDecimal total = payment.getAmount();

        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(PaymentStatus.PENDING)
                .amount(total)
                .message("Realiza la transferencia QR")
                .redirectUrl("/api/payments/" + payment.getId() + "/confirm")
                .build();
    }

    @Transactional
    public void handleWebhook(String payload, String signature) {
        log.info("Webhook recibido: signature={}", signature);
        log.debug("Payload: {}", payload);
    }

    @Transactional
    public PaymentResponseDTO confirmQRPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + paymentId));

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        orderService.markAsPaid(payment.getOrder().getTable().getId());

        log.info("Pago QR confirmado: ${}", payment.getAmount());

        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .status(PaymentStatus.COMPLETED)
                .amount(payment.getAmount())
                .message("Pago confirmado exitosamente")
                .build();
    }
}
