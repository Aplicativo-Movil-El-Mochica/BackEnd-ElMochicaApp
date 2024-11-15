package com.mochica.AppDelivery.DTO;

import lombok.Data;

@Data
public class FormtokenResponseDTO {
    private String formtoken;

    public FormtokenResponseDTO(String formtoken) {
        this.formtoken = formtoken;
    }
}
