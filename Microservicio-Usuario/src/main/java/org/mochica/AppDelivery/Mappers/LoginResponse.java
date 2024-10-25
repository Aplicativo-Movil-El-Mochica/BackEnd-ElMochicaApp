package org.mochica.AppDelivery.Mappers;

import lombok.Data;

@Data
public class LoginResponse {
    String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
