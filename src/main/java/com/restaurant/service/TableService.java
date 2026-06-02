package com.restaurant.service;

import com.restaurant.dto.CreateTableRequest;
import com.restaurant.dto.TableResponse;
import com.restaurant.exception.InvalidTokenException;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.TableStatus;
import com.restaurant.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    @Transactional
    public TableResponse createTable(CreateTableRequest request, String baseUrl) {
        if (tableRepository.existsByTableNumber(request.getTableNumber())) {
            throw new IllegalArgumentException("El número de mesa ya existe: " + request.getTableNumber());
        }

        String token = UUID.randomUUID().toString();

        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity() != null ? request.getCapacity() : 4);
        table.setStatus(TableStatus.AVAILABLE);
        table.setSessionToken(token);
        table.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
        table.setQrCode(baseUrl + "/api/tables/" + table.getId() + "/qr");

        tableRepository.save(table);

        table.setQrCode(baseUrl + "/api/tables/" + table.getId() + "/qr");
        tableRepository.save(table);

        return TableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus().name())
                .qrUrl(table.getQrCode())
                .build();
    }

    public List<TableResponse> getAllTables() {
        return tableRepository.findAll().stream()
                .map(t -> TableResponse.builder()
                        .id(t.getId())
                        .tableNumber(t.getTableNumber())
                        .capacity(t.getCapacity())
                        .status(t.getStatus().name())
                        .qrUrl(t.getQrCode())
                        .build())
                .toList();
    }

    public void validateToken(Long tableId, String token) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new InvalidTokenException("Mesa no encontrada"));

        if (table.getSessionToken() == null || !table.getSessionToken().equals(token)) {
            throw new InvalidTokenException("Token de sesión inválido para la mesa " + table.getTableNumber());
        }

        if (table.getTokenExpiresAt() != null && table.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El token de sesión expiró para la mesa " + table.getTableNumber());
        }
    }

    @Transactional
    public void updateStatus(Long tableId, TableStatus newStatus) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + tableId));
        table.setStatus(newStatus);

        if (newStatus == TableStatus.AVAILABLE) {
            regenerateToken(table);
        }

        tableRepository.save(table);
    }

    @Transactional
    public void regenerateToken(RestaurantTable table) {
        table.setSessionToken(UUID.randomUUID().toString());
        table.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
    }

    public RestaurantTable findById(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + tableId));
    }
}
