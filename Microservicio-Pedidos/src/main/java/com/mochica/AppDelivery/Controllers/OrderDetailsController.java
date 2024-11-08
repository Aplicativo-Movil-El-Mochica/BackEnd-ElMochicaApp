package com.mochica.AppDelivery.Controllers;

import com.mochica.AppDelivery.DTO.AddProductDTO;
import com.mochica.AppDelivery.Service.Impl.OrderDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/cart")
@Validated
public class OrderDetailsController {

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @PostMapping("/aggproduct")
    public ResponseEntity<?> aggproduct(@Valid @RequestBody AddProductDTO addProductDTO) throws InterruptedException, ExecutionException{
        try{
            String success = orderDetailService.addProduct(addProductDTO);
            if (success == "success") {
                return new ResponseEntity<>("Product added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(success, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity<>("Error adding product: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getsubtotal/{userid}")
    public ResponseEntity<?> getsubtotal(@RequestParam String userid) throws InterruptedException, ExecutionException{
        try{
            Integer success = orderDetailService.calcularSubTotal(userid);

                return new ResponseEntity<>(success, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Error adding product: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
