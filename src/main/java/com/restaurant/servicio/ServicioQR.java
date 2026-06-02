package com.restaurant.servicio;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ServicioQR {

    public byte[] generarQR(Long mesaId, String baseUrl, String token) throws Exception {
        String url = baseUrl + "/menu/" + mesaId + "?token=" + token;

        QRCodeWriter escritor = new QRCodeWriter();
        BitMatrix matriz = escritor.encode(url, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matriz, "PNG", salida);
        return salida.toByteArray();
    }
}
