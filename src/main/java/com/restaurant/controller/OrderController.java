package com.restaurant.controller;

import com.restaurant.dto.AddItemRequest;
import com.restaurant.dto.OrderSummaryDTO;
import com.restaurant.service.OrderService;
import com.restaurant.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final TableService tableService;

    @GetMapping("/table/{tableId}")
    public ResponseEntity<OrderSummaryDTO> getOrderSummary(
            @PathVariable Long tableId,
            @RequestHeader(value = "X-Session-Token", required = false) String token) {

        tableService.validateToken(tableId, token);
        return ResponseEntity.ok(orderService.getOrderSummary(tableId));
    }

    @PostMapping("/table/{tableId}/items")
    public ResponseEntity<Void> addItem(
            @PathVariable Long tableId,
            @RequestHeader("X-Session-Token") String token,
            @RequestBody AddItemRequest request) {

        tableService.validateToken(tableId, token);
        orderService.addItem(tableId, request.getMenuItemId(), request.getQuantity(), request.getNotes());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/table/{tableId}")
    public ResponseEntity<Void> createOrder(
            @PathVariable Long tableId,
            @RequestHeader("X-Session-Token") String token) {

        tableService.validateToken(tableId, token);
        orderService.createOrderForTable(tableId);
        return ResponseEntity.ok().build();
    }
}
