package org.mochica.AppDelivery.Service.Impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import org.mochica.AppDelivery.Config.JwtTokenUtil;
import org.mochica.AppDelivery.DTO.LoginDTO;
import org.mochica.AppDelivery.DTO.RegisterDTO;
import org.mochica.AppDelivery.Firebase.FBInitialize;
import org.mochica.AppDelivery.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UsuarioServiceImpl implements UserService {
    @Autowired
    private FBInitialize fbInitialize;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<RegisterDTO> list() {
        return List.of();
    }

    @Override
    public Boolean add(RegisterDTO registerDTO) {
        try {
            CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

            // Verificar si el email ya está registrado
            ApiFuture<QuerySnapshot> emailFuture = usersCollection.whereEqualTo("email", registerDTO.getEmail()).get();
            List<QueryDocumentSnapshot> emailDocuments = emailFuture.get().getDocuments();

            if (!emailDocuments.isEmpty()) {
                System.out.println("El email ya está registrado.");
                return false;  // Email ya registrado
            }

            // Verificar si el DNI ya está registrado
            ApiFuture<QuerySnapshot> dniFuture = usersCollection.whereEqualTo("dni", registerDTO.getDni()).get();
            List<QueryDocumentSnapshot> dniDocuments = dniFuture.get().getDocuments();

            if (!dniDocuments.isEmpty()) {
                System.out.println("El DNI ya está registrado.");
                return false;  // DNI ya registrado
            }

            // Verificar si el número de teléfono ya está registrado
            ApiFuture<QuerySnapshot> phoneFuture = usersCollection.whereEqualTo("phone", registerDTO.getPhone()).get();
            List<QueryDocumentSnapshot> phoneDocuments = phoneFuture.get().getDocuments();

            if (!phoneDocuments.isEmpty()) {
                System.out.println("El teléfono ya está registrado.");
                return false;  // Teléfono ya registrado
            }

            // Si las verificaciones pasan, se procede a registrar el nuevo usuario
            String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());  // Cifrar la contraseña

            // Crear los datos del usuario para guardar en Firestore
            Map<String, Object> docData = new HashMap<>();
            docData.put("email", registerDTO.getEmail());
            docData.put("password", encodedPassword);  // Guardar la contraseña cifrada
            docData.put("name", registerDTO.getName());
            docData.put("dni", registerDTO.getDni());
            docData.put("phone", registerDTO.getPhone());

            // Guardar el nuevo usuario en Firestore
            ApiFuture<WriteResult> writeResultApiFuture = usersCollection.document().create(docData);

            if (writeResultApiFuture.get() != null) {
                return Boolean.TRUE;  // Registro exitoso
            }

            return Boolean.FALSE;  // Error al guardar en Firestore

        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            return Boolean.FALSE;  // Error en la operación de registro
        }
    }

    @Override
    public String login(LoginDTO loginDTO) {
        try {
            CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

            // Buscar el usuario por email
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("email", loginDTO.getEmail()).get();
            QuerySnapshot querySnapshot = future.get();

            // Obtener el resultado
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            if (!documents.isEmpty()) {
                QueryDocumentSnapshot document = documents.get(0);
                String storedPassword = document.getString("password");

                // Comparar contraseñas usando bcrypt para mayor seguridad
                if (passwordEncoder.matches(loginDTO.getPassword(), storedPassword)) {
                    // Generar el JWT usando el email como subject
                    String jwt = jwtTokenUtil.generateToken(loginDTO.getEmail());
                    return jwt;  // Devolver el token al cliente
                } else {
                    return "Contraseña incorrecta";
                }
            } else {
                return "Usuario no encontrado";
            }

        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            return "Error en el login";
        }
    }


    @Override
    public Boolean update(Long id, RegisterDTO registerDTO) {
        return null;
    }

    @Override
    public Boolean delete(Long id, RegisterDTO registerDTO) {
        return null;
    }
}
