package com.mochica.AppDelivery.Entity;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;

@Data
public class OrderDetail {

    private String id;

    @PropertyName("ProductName")
    private String productName;

    @PropertyName("Amount")
    private int amount;

    @PropertyName("Price")
    private int price;

    @PropertyName("Availability")
    private int availability;


}
