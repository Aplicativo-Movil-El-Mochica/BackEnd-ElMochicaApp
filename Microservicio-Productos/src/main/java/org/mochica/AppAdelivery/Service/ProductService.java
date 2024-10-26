package org.mochica.AppAdelivery.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.mochica.AppAdelivery.DTO.ProductCategoryDTO;
import org.mochica.AppAdelivery.DTO.ProductDispositionDTO;
import org.mochica.AppAdelivery.DTO.ProductNameDTO;
import org.mochica.AppAdelivery.DTO.ProductStockDTO;
import org.mochica.AppAdelivery.Entity.Product;
import org.mochica.AppAdelivery.Firebase.FBInitialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {

    @Autowired
    private FBInitialize fbInitialize;

    private Firestore getFirestore() {
        return fbInitialize.getFirestore();
    }

    public String saveProduct(Product product) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = getFirestore();
        ApiFuture<DocumentReference> future = dbFirestore.collection("products").add(product);
        return future.get().getId();
    }

    public Product getProduct(Long productId) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = getFirestore();
        DocumentReference documentReference = dbFirestore.collection("products").document(productId.toString());
        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = documentReference.get();
        com.google.cloud.firestore.DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Product.class);
        }
        return null;
    }

    public String deleteProduct(Long productId) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = getFirestore();
        ApiFuture<com.google.cloud.firestore.WriteResult> writeResult = dbFirestore.collection("products").document(productId.toString()).delete();
        return "Product with ID " + productId + " has been deleted";
    }

    public void updateStock(ProductStockDTO productStockDTO) throws InterruptedException, ExecutionException {
        Product product = getProduct(productStockDTO.getProductId());
        if (product != null) {
            product.actualizarStock(productStockDTO.getCantidad());
            saveProduct(product);
        }
    }

    public int getDisponibility(ProductDispositionDTO productDisponibilityDTO) throws InterruptedException, ExecutionException {
        Product product = getProduct(productDisponibilityDTO.getProductId());
        if (product != null) {
            return product.obtenerDisponibilidad();
        }
        return 0;
    }

    public void addCategory(ProductCategoryDTO productCategoryDTO) throws InterruptedException, ExecutionException {
        Product product = getProduct(productCategoryDTO.getProductId());
        if (product != null) {
            product.agregarCategoria(productCategoryDTO.getCategori());
            saveProduct(product);
        }
    }

    public Product findProductByName(ProductNameDTO productNameDTO) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = getFirestore();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection("products").get();
        List<Product> products = future.get().toObjects(Product.class);
        for (Product product : products) {
            if (product.buscarProductoPorNombre(productNameDTO.getProductName())) {
                return product;
            }
        }
        return null;
    }
}