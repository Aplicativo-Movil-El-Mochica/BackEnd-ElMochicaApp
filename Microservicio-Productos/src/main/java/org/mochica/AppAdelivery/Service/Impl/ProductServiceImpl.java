package org.mochica.AppAdelivery.Service.Impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.mochica.AppAdelivery.DTO.*;
import org.mochica.AppAdelivery.Entity.Categori;
import org.mochica.AppAdelivery.Entity.Product;
import org.mochica.AppAdelivery.Firebase.FBInitialize;
import org.mochica.AppAdelivery.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private FBInitialize fbInitialize;

    private Firestore getFirestore() {
        return fbInitialize.getFirestore();
    }

    private String collectionproduct = "dishes";

    public Boolean saveProduct(SaveProductDTO saveProductDTO) throws InterruptedException, ExecutionException {
        CollectionReference dishesCollection = fbInitialize.getFirestore().collection(collectionproduct);
        Map<String, Object> docData = new HashMap<>();
        docData.put("ProductName", saveProductDTO.getProductName());
        docData.put("Description", saveProductDTO.getDescription());
        docData.put("Price", saveProductDTO.getPrice());
        docData.put("Availability", saveProductDTO.getAvailability());
        docData.put("Category", saveProductDTO.getCategory().name());

        ApiFuture<WriteResult> writeResultApiFuture = dishesCollection.document().create(docData);
        if (writeResultApiFuture.get() != null) {

            return Boolean.TRUE;  // Registro exitoso
        }

        return Boolean.FALSE;  // Error al guardar en Firestore

    }

    public List<Product> getProduct(String productName) throws InterruptedException, ExecutionException {
        CollectionReference dishesCollection = fbInitialize.getFirestore().collection(collectionproduct);
        ApiFuture<QuerySnapshot> productNameFuture = dishesCollection.whereEqualTo("ProductName", productName).get();
        List<QueryDocumentSnapshot> productNameDocuments = productNameFuture.get().getDocuments();

        List<Product> products = new ArrayList<>();
        for (QueryDocumentSnapshot document : productNameDocuments) {
            Product product = document.toObject(Product.class);
            product.setId(document.getId());
            products.add(product);
        }

        return products;
    }

    public String deleteProduct(String productId) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = getFirestore();
        ApiFuture<com.google.cloud.firestore.WriteResult> writeResult = dbFirestore.collection(collectionproduct).document(productId.toString()).delete();
        return "Product with ID " + productId + " has been deleted";
    }

    public Boolean updateStock(ProductStockDTO productStockDTO) throws InterruptedException, ExecutionException {
        CollectionReference productsCollection = fbInitialize.getFirestore().collection(collectionproduct);
        Query query = productsCollection.whereEqualTo("ProductName", productStockDTO.getProductName());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            System.out.println("Producto no encontrado.");
            return Boolean.FALSE;  // Producto no encontrado
        }

        // Paso 2: Obtener el ID del primer documento encontrado
        DocumentReference productRef = documents.get(0).getReference();

        // Paso 3: Actualizar el campo `availability`
        ApiFuture<WriteResult> future = productRef.update("Availability", productStockDTO.getNewavailability());

        // Confirmar si la actualización fue exitosa
        return future.get() != null;
    }

    public int getDisposition(String productName) throws InterruptedException, ExecutionException {
        System.out.println(productName);
        List<Product> products = getProduct(productName);

        // Si la lista no está vacía, obtén el primer producto y devuelve su disponibilidad
        if (!products.isEmpty()) {
            return products.get(0).obtenerDisponibilidad();
        }

        // Si no se encontró el producto, devuelve 0
        return 0;
    }

    public Boolean updateCategory(ProductCategoryDTO productCategoryDTO) throws InterruptedException, ExecutionException {
        // Paso 1: Realizar una consulta para obtener el documento por `ProductName`
        CollectionReference productsCollection = fbInitialize.getFirestore().collection(collectionproduct); // Asegúrate de usar la colección correcta
        Query query = productsCollection.whereEqualTo("ProductName", productCategoryDTO.getProductName());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            System.out.println("Producto no encontrado.");
            return Boolean.FALSE;  // Producto no encontrado
        }

        // Paso 2: Obtener el ID del primer documento encontrado
        DocumentReference productRef = documents.get(0).getReference();

        // Paso 3: Actualizar el campo `Category`
        ApiFuture<WriteResult> future = productRef.update("Category", productCategoryDTO.getNewCategory().name());

        // Confirmar si la actualización fue exitosa
        return future.get() != null;
    }

    public Product findProductByName(ProductNameDTO productNameDTO) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = getFirestore();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection(collectionproduct)
                .whereEqualTo("ProductName", productNameDTO.getProductName()).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        // Verifica si no hay resultados y devuelve null
        if (documents.isEmpty()) {
            return null;
        }

        // Obtén el primer documento y conviértelo a un objeto Product
        QueryDocumentSnapshot document = documents.get(0);
        Product product = document.toObject(Product.class);
        product.setId(document.getId()); // Asigna el ID del documento Firestore al campo id del objeto Product

        return product;
    }


    public List<Product> findProductsByCategory(Categori category) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = getFirestore();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection(collectionproduct)
                .whereEqualTo("Category", category.name()) // Filtramos directamente por categoría
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Product> products = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Product product = document.toObject(Product.class);
            product.setId(document.getId()); // Asigna el ID del documento Firestore al campo id de Product
            products.add(product);
        }

        return products;
    }
}