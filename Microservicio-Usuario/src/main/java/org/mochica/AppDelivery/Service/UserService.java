package org.mochica.AppDelivery.Service;

import org.mochica.AppDelivery.DTO.LoginDTO;
import org.mochica.AppDelivery.DTO.RegisterDTO;
import org.mochica.AppDelivery.Mappers.LoginResponse;

import java.util.List;

public interface UserService {

    List<RegisterDTO> list();

    String add(RegisterDTO registerDTO);
    LoginResponse login(LoginDTO loginDTO);
    Boolean update(Long id, RegisterDTO registerDTO);
    Boolean delete(Long id, RegisterDTO registerDTO);
    String searchdni(Long dni);


}
