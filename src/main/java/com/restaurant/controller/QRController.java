package com.restaurant.controller;

import com.restaurant.service.QRService;
import com.restaurant.service.TableService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;
    private final TableService tableService;

    @GetMapping("/tables/{id}/qr")
    public ResponseEntity<byte[]> getQR(@PathVariable Long id, HttpServletRequest req) throws Exception {
        var table = tableService.findById(id);
        String base = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        byte[] qr = qrService.generateQR(table.getId(), base, table.getSessionToken());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qr);
    }
}
