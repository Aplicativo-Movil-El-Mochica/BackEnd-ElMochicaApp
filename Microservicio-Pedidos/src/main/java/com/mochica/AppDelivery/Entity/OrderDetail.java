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

    @PropertyName("PriceTotal")
    private int price;

    @PropertyName("PriceUnit")
    private int priceUnit;

    @PropertyName("Availability")
    private int availability;


}
