package com.mochica.AppDelivery.Service;

import com.mochica.AppDelivery.DTO.FormtokenResponseDTO;
import com.mochica.AppDelivery.DTO.InitiatePaymentDTO;
import com.mochica.AppDelivery.Entity.Order;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OrderService {

    FormtokenResponseDTO generateFormToken(InitiatePaymentDTO initiatePaymentDTO) throws Exception;
    List<Order> obtenerOrder(String userId) throws ExecutionException, InterruptedException;
}
