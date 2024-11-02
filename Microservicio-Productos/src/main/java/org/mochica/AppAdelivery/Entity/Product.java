package org.mochica.AppAdelivery.Entity;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

    private String id;

    @PropertyName("ProductName")
    private String productName;

    @PropertyName("Description")
    private String description;

    @PropertyName("Price")
    private Long price;

    @PropertyName("Availability")
    private int availability;

    @PropertyName("Category")
    private Categori category;


    public void actualizarStock(int cantidad) {
        this.availability += cantidad;
    }

    public int obtenerDisponibilidad() {
        return this.availability;
    }

    public void agregarCategoria(Categori categori) {
        this.category = categori;
    }

    public boolean buscarProductoPorNombre(String name) {
        return this.productName.equalsIgnoreCase(name);
    }
}