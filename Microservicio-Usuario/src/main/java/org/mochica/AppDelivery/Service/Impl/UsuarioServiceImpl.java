package org.mochica.AppDelivery.Service.Impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.val;
import org.mochica.AppDelivery.Config.EncryptionUtil;
import org.mochica.AppDelivery.Config.JwtTokenUtil;
import org.mochica.AppDelivery.DTO.*;
import org.mochica.AppDelivery.Firebase.FBInitialize;
import org.mochica.AppDelivery.Mappers.LoginResponse;
import org.mochica.AppDelivery.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

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

    @Autowired
    private UserDetailsService userDetailsService;

    public UsuarioServiceImpl(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    @Override
    public List<RegisterDTO> list() {
        return List.of();
    }

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Override
    public String add(RegisterDTO registerDTO) {
        try {
            CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

            // Verificar si el email ya está registrado
            ApiFuture<QuerySnapshot> emailFuture = usersCollection.whereEqualTo("email", registerDTO.getEmail()).get();
            List<QueryDocumentSnapshot> emailDocuments = emailFuture.get().getDocuments();

            if (!emailDocuments.isEmpty()) {
                System.out.println("El email ya está registrado.");
                return "El email ya está registrado";  // Email ya registrado
            }

            // Verificar si el DNI ya está registrado
            ApiFuture<QuerySnapshot> dniFuture = usersCollection.whereEqualTo("dni", registerDTO.getDni()).get();
            List<QueryDocumentSnapshot> dniDocuments = dniFuture.get().getDocuments();

            if (!dniDocuments.isEmpty()) {
                System.out.println("El DNI ya está registrado.");
                return "El DNI ya está registrado";  // DNI ya registrado
            }

            // Verificar si el número de teléfono ya está registrado
            ApiFuture<QuerySnapshot> phoneFuture = usersCollection.whereEqualTo("phone", registerDTO.getPhone()).get();
            List<QueryDocumentSnapshot> phoneDocuments = phoneFuture.get().getDocuments();

            if (!phoneDocuments.isEmpty()) {
                System.out.println("El teléfono ya está registrado.");
                return "El teléfono ya está registrado";  // Teléfono ya registrado
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
                return "Usuario registrado exitosamente.";  // Registro exitoso
            }

            return "Error en el registro";  // Error al guardar en Firestore

        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            return "Error en el registro";  // Error en la operación de registro
        }
    }

    @Override
    public LoginResponse login(LoginDTO loginDTO) {
        try {
            CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("email", loginDTO.getEmail()).get();
            QuerySnapshot querySnapshot = future.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            if (!documents.isEmpty()) {
                QueryDocumentSnapshot document = documents.get(0);
                String storedPassword = document.getString("password");
                int dniStored = document.getLong("dni").intValue();

                if (passwordEncoder.matches(loginDTO.getPassword(), storedPassword)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

                    String jwt = jwtTokenUtil.generateToken(userDetails);
                    String jwtEncrypted = EncryptionUtil.encrypt(jwt);

                    return new LoginResponse(jwtEncrypted, dniStored);
                } else {
                    throw new BadCredentialsException("Contraseña incorrecta");
                }
            } else {
                throw new BadCredentialsException("Usuario no registrado");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error al acceder a la base de datos: " + e.getMessage());
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

    @Override
    public SearchDniDTO searchdni(Long dni) {
        try {
            CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

            // Buscar el usuario por DNI
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("dni", dni).get();
            QuerySnapshot querySnapshot = future.get();

            // Obtener el resultado
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            if (!documents.isEmpty()) {
                QueryDocumentSnapshot document = documents.get(0);
                String documentId = document.getId();
                String name = document.getString("name");
                return new SearchDniDTO(name, documentId);
            } else {
                // Caso en que no se encontró un usuario con el DNI
                return null;
            }
        } catch (ExecutionException | InterruptedException e) {
            // En caso de que haya un error de ejecución o interrupción
            return null;
        }
    }

    @Override
    public Boolean saveAddress(AddressDTO addressDTO) {
        try {
            // Referencia a la colección de usuarios
            CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

            // Referencia al documento del usuario según el ID proporcionado
            DocumentReference userDoc = usersCollection.document(addressDTO.getUserId());

            // Crear un mapa con los campos a añadir
            Map<String, Object> data = new HashMap<>();
            data.put("address", addressDTO.getAddress());
            data.put("reference", addressDTO.getReference());

            // Intentar establecer los campos en el documento del usuario
            ApiFuture<WriteResult> writeResult = userDoc.set(data, SetOptions.merge());

            // Obtener el resultado de la operación para verificar si fue exitosa
            writeResult.get();

            // Si no ocurre ninguna excepción, la adición fue exitosa
            return true;
        } catch (ExecutionException | InterruptedException e) {
            // Manejar la excepción si ocurre un error durante la adición
            e.printStackTrace();
            return false;
        }
    }


    public Boolean updateaddress(AddressDTO addressDTO) {
        try {
            // Referencia a la colección de usuarios
            CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

            // Referencia al documento del usuario según el ID proporcionado
            DocumentReference userDoc = usersCollection.document(addressDTO.getUserId());

            // Crear un mapa con los campos a actualizar
            Map<String, Object> updates = new HashMap<>();
            updates.put("address", addressDTO.getAddress());
            updates.put("reference", addressDTO.getReference());

            // Intentar actualizar los campos en el documento del usuario
            ApiFuture<WriteResult> writeResult = userDoc.update(updates);

            // Obtener el resultado de la operación para verificar si fue exitosa
            writeResult.get();

            // Si no ocurre ninguna excepción, la actualización fue exitosa
            return true;
        } catch (ExecutionException | InterruptedException e) {
            // Manejar la excepción si ocurre un error durante la actualización
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendVoucher(EmailDTO emailDTO) {


        try {

            Context context = new Context();
            context.setVariable("mensaje", emailDTO.getNombre());
            context.setVariable("cc", emailDTO.getCc());
            context.setVariable("dni", emailDTO.getDni());
            context.setVariable("tipoPedido", emailDTO.getTipoPedido());
            context.setVariable("direccion", emailDTO.getDireccion());
            context.setVariable("fechaPedido", emailDTO.getFechaPedido());
            context.setVariable("resumenPedido", emailDTO.getResumenPedido());
            context.setVariable("total", emailDTO.getTotal());


            String html = templateEngine.process("Correo", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailDTO.getDestinatario());
            helper.setSubject("Boleta de Venta");
            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage(), e);
        }
    }


}
