package org.mochica.AppAdelivery.DTO;

import lombok.Data;
import org.mochica.AppAdelivery.Entity.Categori;

@Data
public class ProductCategoryDTO {

    private String productName;
    private Categori newCategory;

}