package org.mochica.AppAdelivery.DTO;

import lombok.Data;
import org.mochica.AppAdelivery.Entity.Categori;

@Data
public class SaveProductDTO {
    private String productName; // Cambiado a camelCase
    private String description; // Cambiado a camelCase
    private Long price;         // Cambiado a camelCase
    private int availability;   // Cambiado a camelCase
    private Categori category;  // Cambiado a camelCase
}
