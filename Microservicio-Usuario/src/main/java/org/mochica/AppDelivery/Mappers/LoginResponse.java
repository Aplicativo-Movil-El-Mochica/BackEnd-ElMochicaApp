package org.mochica.AppDelivery.Mappers;

import lombok.Data;

@Data
public class LoginResponse {
    String token;
    int dni;

    public LoginResponse(String token, int dni) {
        this.token = token;
        this.dni = dni;
    }
}
