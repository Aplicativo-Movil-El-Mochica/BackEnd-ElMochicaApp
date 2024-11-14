package org.mochica.AppDelivery.Controllers;

import org.mochica.AppDelivery.DTO.AddressDTO;
import org.mochica.AppDelivery.DTO.LoginDTO;
import org.mochica.AppDelivery.DTO.RegisterDTO;
import org.mochica.AppDelivery.DTO.SearchDniDTO;
import org.mochica.AppDelivery.Mappers.LoginResponse;
import org.mochica.AppDelivery.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

@RestController
@RequestMapping(value = "/user")

public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<?> add(@RequestBody RegisterDTO registerDTO){
        try {
            String isRegistered = userService.add(registerDTO);
            if (isRegistered.equals("Usuario registrado exitosamente.")) {
                return ResponseEntity.ok("Usuario registrado exitosamente.");
            } else {
                return ResponseEntity.status(409).body(isRegistered);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en el registro: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            // Llamar al servicio de login para autenticar al usuario
            LoginResponse loginResponse = userService.login(loginDTO);

            if (loginResponse == null) {
                return ResponseEntity.status(401).body("Usuario no encontrado o contraseña incorrecta");  // Error de autenticación
            }

            // Si el login es exitoso, devolver el JWT token y dni en LoginResponseDTO
            return ResponseEntity.ok(loginResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Error en la autenticación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @GetMapping("/search/{dni}")
    public ResponseEntity<?> searchdni(@PathVariable Long dni){
        try {
            SearchDniDTO value = userService.searchdni(dni);
            if (value == null) {
                return ResponseEntity.status(401).body("Usuario no encontrado");  // Error de autenticación
            }
            return ResponseEntity.ok(value);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la consulta: " + e.getMessage());
        }
    }

    @PostMapping("/saveaddress")
    public ResponseEntity<?> saveaddress(@RequestBody AddressDTO addressDTO) {
        try {
            // Llamar al servicio de login para autenticar al usuario
            Boolean loginResponse = userService.saveAddress(addressDTO);

            if (!loginResponse) {
                return ResponseEntity.status(401).body("Usuario no encontrado");  // Error de autenticación
            }

            // Si el login es exitoso, devolver el JWT token y dni en LoginResponseDTO
            return ResponseEntity.ok(loginResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Error en la autenticación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @PostMapping("/updateaddress")
    public ResponseEntity<?> updateaddress(@RequestBody AddressDTO addressDTO) {
        try {
            // Llamar al servicio de login para autenticar al usuario
            Boolean loginResponse = userService.updateaddress(addressDTO);

            if (!loginResponse) {
                return ResponseEntity.status(401).body("Usuario no encontrado");  // Error de autenticación
            }

            // Si el login es exitoso, devolver el JWT token y dni en LoginResponseDTO
            return ResponseEntity.ok(loginResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Error en la autenticación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

}
