package com.mochica.AppDelivery.Entity;

import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
public class Order {

    private String id;
    private String userId;
    private Date orderDate;
    private Time timeOrder;
    private Double total;
    private OrderStatus orderStatus;
    private List<OrderDetail> details;

    public void updateOrderStatus(OrderStatus status) {
        this.orderStatus = status;
    }
}
