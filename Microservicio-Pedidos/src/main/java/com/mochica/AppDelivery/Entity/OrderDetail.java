package com.mochica.AppDelivery.Entity;

import lombok.Data;

@Data
public class OrderDetail {

    private String productName;
    private int amount;
    private int price;
    private int availability;

    public double getSubtotal() {
        return this.amount * this.price;
    }


}
