package org.mochica.AppDelivery.Mappers;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
