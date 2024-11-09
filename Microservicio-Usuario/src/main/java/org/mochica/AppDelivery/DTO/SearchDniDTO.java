package org.mochica.AppDelivery.DTO;

import lombok.Data;

@Data
public class SearchDniDTO {
    private String name;
    private String id;


    public SearchDniDTO(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
