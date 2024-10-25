package org.mochica.AppDelivery.Config;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.mochica.AppDelivery.Firebase.FBInitialize;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RefreshTokenService {


    // Generar un refresh token aleatorio
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();  // Esto es solo un ejemplo, puedes usar otras estrategias para generar el refresh token
    }

    // Guardar el refresh token en Firestore
    public void saveRefreshToken(String email, String refreshToken) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference users = db.collection("users");

        ApiFuture<WriteResult> future = users.document(email).update("refreshToken", refreshToken);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Verificar y validar el refresh token en Firestore
    public boolean validateRefreshToken(String email, String refreshToken) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = db.collection("users").document(email).get().get();

        if (document.exists()) {
            String storedRefreshToken = document.getString("refreshToken");
            return storedRefreshToken.equals(refreshToken);
        }
        return false;
    }
}
