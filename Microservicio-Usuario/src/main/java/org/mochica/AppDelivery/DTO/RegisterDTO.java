package org.mochica.AppDelivery.DTO;

import lombok.Data;

@Data
public class RegisterDTO {

    private int id;
    private String name;
    private String email;
    private Long dni;
    private String password;
    private Long phone;
}
