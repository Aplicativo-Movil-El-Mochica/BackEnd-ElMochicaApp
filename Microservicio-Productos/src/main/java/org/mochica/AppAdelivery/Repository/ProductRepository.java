package org.mochica.AppAdelivery.Repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.mochica.AppAdelivery.Entity.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ProductRepository {

    private static final String COLLECTION_NAME = "products";

    // Guardar producto
    public String saveProduct(Product product) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(product.getId().toString()).set(product);
        return "Product saved!";
    }

    // Obtener producto por ID
    public Product getProduct(Long productId) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        return dbFirestore.collection(COLLECTION_NAME).document(productId.toString()).get().get().toObject(Product.class);
    }

    // Eliminar producto
    public String deleteProduct(Long productId) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection(COLLECTION_NAME).document(productId.toString()).delete();
        return "Product deleted!";
    }

    // Obtener todos los productos
    public List<Product> getAllProducts() throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        QuerySnapshot snapshot = dbFirestore.collection(COLLECTION_NAME).get().get();
        List<Product> productList = new ArrayList<>();

        snapshot.forEach(document -> {
            Product product = document.toObject(Product.class);
            productList.add(product);
        });
        return productList;
    }
}
