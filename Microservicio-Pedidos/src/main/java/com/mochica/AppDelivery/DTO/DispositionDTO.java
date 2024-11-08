package com.mochica.AppDelivery.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DispositionDTO {

    @NotBlank(message = "Nombre Vacio")
    private String productName;
}
