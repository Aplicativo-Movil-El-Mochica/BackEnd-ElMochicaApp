package com.mochica.AppDelivery.DTO;

import lombok.Data;

@Data
public class ModificarCarritoDTO {
    private String cartProductId;
    private int newamount;
    private String action;

}
