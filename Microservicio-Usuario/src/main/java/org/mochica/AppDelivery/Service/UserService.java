package org.mochica.AppDelivery.Service;

import org.mochica.AppDelivery.DTO.AddressDTO;
import org.mochica.AppDelivery.DTO.LoginDTO;
import org.mochica.AppDelivery.DTO.RegisterDTO;
import org.mochica.AppDelivery.DTO.SearchDniDTO;
import org.mochica.AppDelivery.Mappers.LoginResponse;

import java.util.List;

public interface UserService {

    List<RegisterDTO> list();

    String add(RegisterDTO registerDTO);
    LoginResponse login(LoginDTO loginDTO);
    Boolean update(Long id, RegisterDTO registerDTO);
    Boolean delete(Long id, RegisterDTO registerDTO);
    SearchDniDTO searchdni(Long dni);
    Boolean saveAddress(AddressDTO addressDTO);
    Boolean updateaddress(AddressDTO addressDTO);
}
