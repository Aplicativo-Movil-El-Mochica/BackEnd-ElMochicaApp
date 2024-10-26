package org.mochica.AppAdelivery.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

    private Long id;

    private String productName;

    private String description;

    private double price;

    private int disposition;

    private Categori categori;


    public void actualizarStock(int cantidad) {
        this.disposition += cantidad;
    }

    public int obtenerDisponibilidad() {
        return this.disposition;
    }

    public void agregarCategoria(Categori categori) {
        this.categori = categori;
    }

    public boolean buscarProductoPorNombre(String name) {
        return this.productName.equalsIgnoreCase(name);
    }
}