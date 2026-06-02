package com.restaurant.service;

import com.restaurant.dto.OrderItemDTO;
import com.restaurant.dto.OrderSummaryDTO;
import com.restaurant.dto.events.OrderUpdateEvent;
import com.restaurant.exception.OrderAlreadyPaidException;
import com.restaurant.model.*;
import com.restaurant.repository.MenuItemRepository;
import com.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableService tableService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public OrderSummaryDTO getOrderSummary(Long tableId) {
        Order order = getActiveOrder(tableId);
        return buildSummary(order);
    }

    @Transactional
    @Retryable(retryFor = {org.springframework.orm.ObjectOptimisticLockingFailureException.class},
               maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void addItem(Long tableId, Long menuItemId, int quantity, String notes) {
        Order order = getActiveOrder(tableId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Item del menú no encontrado: " + menuItemId));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(menuItem.getPrice());
        orderItem.setNotes(notes);

        order.getItems().add(orderItem);
        orderRepository.save(order);

        RestaurantTable table = order.getTable();
        BigDecimal total = getOrderTotal(order.getId());

        OrderUpdateEvent event = new OrderUpdateEvent(
                table.getId(), table.getTableNumber(), order.getId(), total, order.getItems().size()
        );
        messagingTemplate.convertAndSend("/topic/tables", event);

        log.info("Item agregado a mesa {}: {}x {} - total: {}",
                table.getTableNumber(), quantity, menuItem.getName(), total);
    }

    @Transactional
    public Order createOrderForTable(Long tableId) {
        RestaurantTable table = tableService.findById(tableId);

        if (table.getStatus() == TableStatus.AVAILABLE) {
            tableService.updateStatus(tableId, TableStatus.OCCUPIED);
        }

        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.OPEN);
        order.setCreatedAt(java.time.LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Transactional
    public Order markAsPaid(Long tableId) {
        Order order = getActiveOrder(tableId);
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(java.time.LocalDateTime.now());
        orderRepository.save(order);

        tableService.updateStatus(tableId, TableStatus.AVAILABLE);

        com.restaurant.dto.events.TableStatusEvent event = new com.restaurant.dto.events.TableStatusEvent(
                tableId, order.getTable().getTableNumber(), TableStatus.AVAILABLE
        );
        messagingTemplate.convertAndSend("/topic/tables", event);

        log.info("Orden pagada: mesa {} - total: {}",
                order.getTable().getTableNumber(), getOrderTotal(order.getId()));
        return order;
    }

    public Order getActiveOrder(Long tableId) {
        return orderRepository.findActiveByTableId(tableId)
                .orElseThrow(() -> new RuntimeException("No hay orden activa para la mesa " + tableId));
    }

    public BigDecimal getOrderTotal(Long orderId) {
        return orderRepository.getOrderTotal(orderId);
    }

    private OrderSummaryDTO buildSummary(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .notes(item.getNotes())
                        .build())
                .toList();

        BigDecimal total = itemDTOs.stream()
                .map(OrderItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderSummaryDTO.builder()
                .orderId(order.getId())
                .tableNumber(order.getTable().getTableNumber())
                .items(itemDTOs)
                .total(total)
                .status(order.getStatus())
                .build();
    }
}
