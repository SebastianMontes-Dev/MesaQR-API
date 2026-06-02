package com.restaurant.controller;

import com.restaurant.dto.CreateTableRequest;
import com.restaurant.dto.TableResponse;
import com.restaurant.service.TableService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping
    public ResponseEntity<TableResponse> createTable(
            @RequestBody CreateTableRequest request,
            HttpServletRequest httpReq) {

        String baseUrl = httpReq.getScheme() + "://" + httpReq.getServerName() + ":" + httpReq.getServerPort();
        TableResponse response = tableService.createTable(request, baseUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getTable(@PathVariable Long id) {
        var table = tableService.findById(id);
        return ResponseEntity.ok(TableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus().name())
                .qrUrl(table.getQrCode())
                .build());
    }
}
