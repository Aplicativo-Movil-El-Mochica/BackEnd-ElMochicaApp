package com.mochica.AppDelivery.Controllers;
import com.mochica.AppDelivery.Service.Impl.IpnVerificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ipn")
public class IPNController {

    @Autowired
    private IpnVerificationServiceImpl ipnVerificationService;

    @PostMapping("/notification")
    public ResponseEntity<String> handleIpnNotification(@RequestBody Map<String, String> payload) {
        // Verificar la firma de la IPN
        if (!ipnVerificationService.verifySignature(payload)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Firma inválida");
        }

        // Procesar la notificación según el estado del pago
        String paymentStatus = payload.get("vads_trans_status");
        if ("AUTHORISED".equals(paymentStatus)) {
            // Lógica para pagos autorizados
            System.out.println("Pago autorizado: " + payload);
        } else if ("REFUSED".equals(paymentStatus)) {
            // Lógica para pagos rechazados
            System.out.println("Pago rechazado: " + payload);
        }
        // Otros estados según sea necesario

        return ResponseEntity.ok("Notificación procesada");
    }

}
