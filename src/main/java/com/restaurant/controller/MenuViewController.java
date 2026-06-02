package com.restaurant.controller;

import com.restaurant.dto.OrderSummaryDTO;
import com.restaurant.service.MenuItemService;
import com.restaurant.service.OrderService;
import com.restaurant.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuViewController {

    private final OrderService orderService;
    private final TableService tableService;
    private final MenuItemService menuItemService;

    @Value("${restaurant.name:Mi Restaurante}")
    private String restaurantName;

    @GetMapping("/{tableId}")
    public String menuView(@PathVariable Long tableId,
                           @RequestParam String token,
                           Model model) {

        tableService.validateToken(tableId, token);

        OrderSummaryDTO order;
        try {
            order = orderService.getOrderSummary(tableId);
        } catch (RuntimeException e) {
            orderService.createOrderForTable(tableId);
            order = orderService.getOrderSummary(tableId);
        }

        model.addAttribute("tableId", tableId);
        model.addAttribute("token", token);
        model.addAttribute("order", order);
        model.addAttribute("restaurantName", restaurantName);
        model.addAttribute("menuItems", menuItemService.getAvailableMenuItems());

        return "menu";
    }
}
