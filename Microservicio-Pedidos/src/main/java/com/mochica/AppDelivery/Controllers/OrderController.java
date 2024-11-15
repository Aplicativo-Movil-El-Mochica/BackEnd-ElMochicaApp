package com.mochica.AppDelivery.Controllers;

import com.mochica.AppDelivery.DTO.FormtokenResponseDTO;
import com.mochica.AppDelivery.DTO.InitiatePaymentDTO;
import com.mochica.AppDelivery.Service.Impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/payment")
@Validated
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    @PostMapping("/formtoken")
    public ResponseEntity<?> processPayment(@RequestBody InitiatePaymentDTO initiatePaymentDTO) {
        try {
            FormtokenResponseDTO formToken = orderService.generateFormToken(initiatePaymentDTO);
            return ResponseEntity.ok(formToken);
        } catch (ExecutionException | InterruptedException e ) {
            return new ResponseEntity<>("Error generate formtoken: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generate formtoken: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
