package com.mochica.AppDelivery.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StockUpdateDTO {

    @NotBlank(message = "NameProduct Vacio")
    private String productName;

    @NotBlank(message = "New Availability Vacio")
    private int newavailability;
}
