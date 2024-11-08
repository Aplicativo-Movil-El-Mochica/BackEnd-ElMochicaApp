package com.mochica.AppDelivery.Service;

import com.mochica.AppDelivery.DTO.AddProductDTO;
import com.mochica.AppDelivery.DTO.ModificarCarritoDTO;
import com.mochica.AppDelivery.Entity.OrderDetail;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OrderDetailService {


    String addProduct(AddProductDTO addProductDTO) throws ExecutionException, InterruptedException;
    Integer calcularSubTotal(String userId);
    List<OrderDetail> obtenerCarrito(String userId) throws ExecutionException, InterruptedException;
    Boolean modificarCarrito(ModificarCarritoDTO modificarCarritoDTO);
}
