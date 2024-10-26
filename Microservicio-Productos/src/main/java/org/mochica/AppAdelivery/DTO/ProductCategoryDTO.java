package org.mochica.AppAdelivery.DTO;

import lombok.Data;
import org.mochica.AppAdelivery.Entity.Categori;

@Data
public class ProductCategoryDTO {

    private Long productId;
    private Categori categori;

}