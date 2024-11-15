package com.mochica.AppDelivery.Service.Impl;

import com.mochica.AppDelivery.Service.IpnVerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

@Service
public class IpnVerificationServiceImpl implements IpnVerificationService {

    @Value("${izipay.secret-key}")  // Define esta clave en application.properties o application.yml
    private String secretKey;

    @Override
    public boolean verifySignature(Map<String, String> payload) {
        try {
            // Ordenar los parámetros alfabéticamente
            TreeMap<String, String> sortedParams = new TreeMap<>(payload);

            // Construir la cadena de datos
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (!"signature".equals(entry.getKey())) { // Excluir el campo "signature"
                    data.append(entry.getValue());
                }
            }
            data.append(secretKey);  // Agregar la clave secreta al final

            // Calcular el hash SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.toString().getBytes(StandardCharsets.UTF_8));

            // Convertir el hash a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Comparar la firma calculada con la recibida
            String calculatedSignature = hexString.toString().toUpperCase();
            String receivedSignature = payload.get("signature").toUpperCase();

            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
