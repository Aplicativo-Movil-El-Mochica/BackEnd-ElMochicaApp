package org.mochica.AppDelivery.Entity;

import lombok.Data;

@Data
public class PedidoItem {

    private String nombre;
    private int cantidad;
    private double precio;

}
