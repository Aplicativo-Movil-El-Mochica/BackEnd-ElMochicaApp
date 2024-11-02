package com.mochica.AppDelivery.Entity;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class Order {

    Date orderDate;
    Time timeOrder;
    Double total;
    OrderStatus orderStatus;
}
