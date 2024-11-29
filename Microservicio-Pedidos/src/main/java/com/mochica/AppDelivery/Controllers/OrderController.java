package com.mochica.AppDelivery.Controllers;

import com.mochica.AppDelivery.DTO.FormtokenResponseDTO;
import com.mochica.AppDelivery.DTO.InitiatePaymentDTO;
import com.mochica.AppDelivery.DTO.UpdateStatusDTO;
import com.mochica.AppDelivery.Entity.Order;
import com.mochica.AppDelivery.Entity.OrderDetail;
import com.mochica.AppDelivery.Entity.OrderStatus;
import com.mochica.AppDelivery.Service.Impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/obtenerOrder/{userid}")
    public ResponseEntity<?> obtenerCarrito(@PathVariable String userid) throws InterruptedException, ExecutionException{
        try{
            List<Order> success = orderService.obtenerOrder(userid);

            return new ResponseEntity<>(success, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Error adding product: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarCounter/{orderId}")
    public ResponseEntity<?> actualizarCounter(@PathVariable String orderId) throws InterruptedException, ExecutionException{
        try{
            Boolean success = orderService.actualizarStatusCounter(orderId);
            if (success){
                return new ResponseEntity<>(success, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);
            }

        }catch (Exception e){
            return new ResponseEntity<>("Error adding product: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarStatus/{orderId}")
    public ResponseEntity<?> actualizarStatus(@PathVariable String orderId,@RequestBody UpdateStatusDTO updateStatusDTO) throws InterruptedException, ExecutionException{
        try{
            Boolean success = orderService.actualizarStatus(orderId, updateStatusDTO);
            if (success){
                return new ResponseEntity<>(success, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);
            }

        }catch (Exception e){
            return new ResponseEntity<>("Error adding product: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
