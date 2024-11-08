package com.mochica.AppDelivery.Service.Impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mochica.AppDelivery.DTO.AddProductDTO;
import com.mochica.AppDelivery.DTO.DispositionDTO;
import com.mochica.AppDelivery.DTO.ModificarCarritoDTO;
import com.mochica.AppDelivery.DTO.StockUpdateDTO;
import com.mochica.AppDelivery.Entity.OrderDetail;
import com.mochica.AppDelivery.Firebase.FBInitialize;
import com.mochica.AppDelivery.Service.OrderDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class OrderDetailServiceServiceImpl implements OrderDetailService {

    @Autowired
    private FBInitialize fbInitialize;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.product.url}")
    private String productoServiceUrl;

    @Value("${api.user.url}")
    private String userServiceUrl;

    private String collection = "orderdetails";
    private String dishescollection = "dishes";

    @Override
    public String addProduct(AddProductDTO addProductDTO) throws ExecutionException, InterruptedException {

        CollectionReference dishesCollection = fbInitialize.getFirestore().collection(collection);

        // Primer paso: obtener availability como entero desde /getDisposition
        DispositionDTO dispositionDTO = new DispositionDTO();
        dispositionDTO.setProductName(addProductDTO.getProductName());

        String urlGetDisposition = productoServiceUrl + "/getDisposition";
        ResponseEntity<Integer> response = restTemplate.postForEntity(urlGetDisposition, dispositionDTO, Integer.class);

        if (response.getStatusCode().is2xxSuccessful()) {
                Integer availability = response.getBody();  // Obtener directamente el valor de availability como Integer
                if (availability == null) {
                    System.out.println("El valor de availability no está presente en la respuesta.");
                    return "Error al obtener la disponibilidad del producto";
                }

                // Calcular el nuevo valor de availability

                if (availability == 0){
                    System.out.println("Sin Stock");
                    return "Sin Stock";
                }
                int newAvailability = availability - addProductDTO.getAmount();

                int newprice = addProductDTO.getPrice() * addProductDTO.getAmount();

            if (newAvailability < 0){
                return "Sin Stock";
            }


            String urlUpdateStock = productoServiceUrl + "/updateStock";

            // Crear el objeto DTO para el PUT
            StockUpdateDTO stockUpdateDTO = new StockUpdateDTO();
            stockUpdateDTO.setProductName(addProductDTO.getProductName());
            stockUpdateDTO.setNewavailability(newAvailability);

            try {
                // Crear la entidad con el DTO y los encabezados necesarios
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<StockUpdateDTO> requestEntity = new HttpEntity<>(stockUpdateDTO, headers);

                // Realizar el PUT
                ResponseEntity<Void> updateStockResponse = restTemplate.exchange(urlUpdateStock, HttpMethod.PUT, requestEntity, Void.class);
                if (updateStockResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Stock actualizado exitosamente en /updateStock.");


                    Map<String, Object> docData = new HashMap<>();
                    docData.put("ProductName", addProductDTO.getProductName());
                    docData.put("Amount", addProductDTO.getAmount());
                    docData.put("Price", newprice);
                    docData.put("UserId", addProductDTO.getUserId());
                    docData.put("Availability", newAvailability);

                    // Agregar el documento en Firebase
                    ApiFuture<WriteResult> writeResultApiFuture = dishesCollection.document().set(docData);
                    writeResultApiFuture.get(); // Esperar hasta que se complete la actualización

                    System.out.println("Producto añadido exitosamente a Firebase con la nueva disponibilidad.");
                    return "success";
                } else {
                    System.out.println("Error al actualizar el stock en /updateStock.");
                    return "Error al actualizar el stock";
                }
            } catch (Exception e) {
                System.out.println("Excepción al realizar el PUT para actualizar el stock: " + e.getMessage());
                return "Error al actualizar el stock";
            }

        } else {
                System.out.println("Error en la respuesta al intentar obtener availability de /getDisposition.");
            return "Error al actualizar el stock";
        }
    }

    @Override
    public Integer calcularSubTotal(String userId) {
        int subtotal = 0;

        try {
            // Obtener la referencia a la colección
            CollectionReference dishesCollection = fbInitialize.getFirestore().collection(collection);

            // Consultar los documentos donde el campo "UserId" coincide con el userId especificado
            ApiFuture<QuerySnapshot> query = dishesCollection.whereEqualTo("UserId", userId).get();

            // Obtener los resultados de la consulta
            QuerySnapshot querySnapshot = query.get();

            // Iterar sobre los documentos y sumar el precio de cada pedido
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                if (document.contains("Price")) {
                    Integer price = document.getLong("Price").intValue(); // Convertir el precio a Integer
                    subtotal += price; // Sumar el precio al subtotal
                }
            }
        } catch (Exception e) {
            System.out.println("Error al calcular el subtotal: " + e.getMessage());
        }

        return subtotal;
    }

    @Override
    public List<OrderDetail> obtenerCarrito(String userId) throws ExecutionException, InterruptedException {
        CollectionReference dishesCollection = fbInitialize.getFirestore().collection(collection);

        ApiFuture<QuerySnapshot> productNameFuture = dishesCollection.whereEqualTo("UserId", userId).get();
        List<QueryDocumentSnapshot> productNameDocuments = productNameFuture.get().getDocuments();

        // Crear la lista donde se almacenarán los detalles de los pedidos
        List<OrderDetail> orderDetailServices = new ArrayList<>();

        // Iterar sobre los documentos obtenidos y agregarlos a la lista
        for (QueryDocumentSnapshot document : productNameDocuments) {
            // Convertir el documento en un objeto OrderDetail
            OrderDetail orderDetail = document.toObject(OrderDetail.class);

            // Establecer el ID del documento en el objeto OrderDetail (si es necesario)
            orderDetail.setId(document.getId());

            // Agregar el objeto OrderDetail a la lista
            orderDetailServices.add(orderDetail);
        }

        // En este punto, orderDetailServices contiene todos los documentos que cumplen la condición
        return orderDetailServices;
    }

    @Override
    public Boolean modificarCarrito(ModificarCarritoDTO modificarCarritoDTO) {
        try {
            // Obtener la referencia de la colección "products"
            CollectionReference dishesCollection = fbInitialize.getFirestore().collection(collection);
            CollectionReference collectiondish = fbInitialize.getFirestore().collection(dishescollection);
            String productId = modificarCarritoDTO.getCartProductId();

            // Consultar el documento específico por productId en el carrito
            DocumentSnapshot document = dishesCollection.document(productId).get().get();

            if (document.exists()) {
                // Obtener los valores actuales en el carrito
                int currentAmount = document.getLong("Amount").intValue();
                int currentPrice = document.getLong("Price").intValue();
                String productName = document.getString("ProductName");

                // Consultar el producto por nombre en la colección de productos
                ApiFuture<QuerySnapshot> query = collectiondish.whereEqualTo("ProductName", productName).get();
                List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                if (documents.isEmpty()) {
                    System.out.println("Producto no encontrado en la colección de productos.");
                    return Boolean.FALSE;
                }

                // Obtener el documento y disponibilidad actual del producto
                DocumentReference productDocumentRef = documents.get(0).getReference();
                int availability = documents.get(0).getLong("Availability").intValue();

                int newAmount = currentAmount;
                int newAvailability = availability;

                // Calcular nuevos valores según la acción
                if (modificarCarritoDTO.getAction().equals("sumar")) {
                    newAmount = currentAmount + modificarCarritoDTO.getNewamount();
                    newAvailability = availability - modificarCarritoDTO.getNewamount();
                } else if (modificarCarritoDTO.getAction().equals("restar")) {
                    newAmount = currentAmount - modificarCarritoDTO.getNewamount();
                    newAvailability = availability + modificarCarritoDTO.getNewamount();
                } else {
                    System.out.println("Acción no válida.");
                    return Boolean.FALSE;
                }

                // Calcular el precio unitario y luego el nuevo price basado en el nuevo amount
                int unitPrice = currentPrice / currentAmount;
                int newPrice = unitPrice * newAmount;

                // Crear un mapa para los datos actualizados en el carrito (solo el nuevo amount y price)
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("Amount", newAmount);
                updatedData.put("Price", newPrice);

                // Crear un mapa para actualizar solo Availability en la colección de productos
                Map<String, Object> updatedDataDish = new HashMap<>();
                updatedDataDish.put("Availability", newAvailability);

                // Actualizar el documento en el carrito
                ApiFuture<WriteResult> writeResult = dishesCollection.document(productId).update(updatedData);
                writeResult.get(); // Esperar hasta que se complete la actualización

                // Actualizar el Availability en la colección de productos
                ApiFuture<WriteResult> writeResultDish = productDocumentRef.update(updatedDataDish);
                writeResultDish.get();

                System.out.println("Producto actualizado exitosamente en el carrito y en la colección de productos.");
                return Boolean.TRUE;
            } else {
                System.out.println("Producto no encontrado en el carrito.");
                return Boolean.FALSE;
            }

        } catch (Exception e) {
            System.out.println("Error al modificar la cantidad del producto en el carrito: " + e.getMessage());
            return false;
        }

    }


}
