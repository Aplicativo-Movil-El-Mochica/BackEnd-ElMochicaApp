package com.mochica.AppDelivery.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductDTO {
    @NotBlank(message = "Nombre de producto vacio")
    private String productName;

    @NotNull(message = "Cantidad vacia")
    private int amount;

    @NotNull(message = "Precio vacio")
    private int price;


    @NotBlank(message = "UserId vacio")
    private String userId;
}
