package com.mochica.AppDelivery.Entity;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
public class Order {

    private String id;

    @PropertyName("UserId")
    private String userId;

    @PropertyName("OrderDate")
    private Date orderDate;

    @PropertyName("Total")
    private Double total;

    @PropertyName("OrderStatus")
    private OrderStatus orderStatus;

    @PropertyName("Details")
    private List<OrderDetail> details;

    private Boolean statusCounter;

    public void updateOrderStatus(OrderStatus status) {
        this.orderStatus = status;
    }
}
