package org.mochica.AppDelivery.Config;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.security.core.userdetails.User;
import org.mochica.AppDelivery.Firebase.FBInitialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private FBInitialize fbInitialize;  // Inyectar la inicialización de Firebase



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

        try {
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("email", email).get();
            QuerySnapshot querySnapshot = future.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            if (!documents.isEmpty()) {
                QueryDocumentSnapshot document = documents.get(0);
                String storedEmail = document.getString("email");
                String storedPassword = document.getString("password");

                return new User(storedEmail, storedPassword, new ArrayList<>());
            } else {
                throw new UsernameNotFoundException("Usuario no encontrado con el correo: " + email);
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new UsernameNotFoundException("Error al consultar el usuario con el correo: " + email, e);
        }
    }
}
