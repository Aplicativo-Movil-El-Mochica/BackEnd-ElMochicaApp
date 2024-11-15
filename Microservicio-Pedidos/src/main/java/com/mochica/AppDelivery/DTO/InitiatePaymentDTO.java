package com.mochica.AppDelivery.DTO;

import lombok.Data;

@Data
public class InitiatePaymentDTO {
    private String userId;
    private double amount;
    private String currency;
    private String orderId;

}
