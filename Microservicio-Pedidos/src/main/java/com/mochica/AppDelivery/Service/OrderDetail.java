package com.mochica.AppDelivery.Service;

import com.mochica.AppDelivery.DTO.AddProductDTO;

import java.util.concurrent.ExecutionException;

public interface OrderDetail {


    String addProduct(AddProductDTO addProductDTO) throws ExecutionException, InterruptedException;
    Integer calcularSubTotal(String userId);
}
