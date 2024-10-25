package org.mochica.AppDelivery.Entity;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Data
public class User implements UserDetails {


    Long Id;
    String name;
    String email;
    String password;
    Long phone;
    String address;
    Long dni;
    String reference;

    public User(Long id, String name, String email, String password, Long phone, String address, Long dni, String reference) {
        Id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.dni = dni;
        this.reference = reference;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna la lista de roles o privilegios que tenga el usuario
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;  // Usamos el correo como nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Lógica para determinar si la cuenta ha expirado
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Lógica para determinar si la cuenta está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Lógica para determinar si las credenciales han expirado
    }

    @Override
    public boolean isEnabled() {
        return true;  // Lógica para determinar si la cuenta está habilitada
    }

}
