package org.mochica.AppDelivery.Firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Service
public class FBInitialize {

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceaccount.json");
            if (serviceAccount == null) {
                throw new FileNotFoundException("El archivo de credenciales no se encontró.");
            }
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://deliverymochica-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}