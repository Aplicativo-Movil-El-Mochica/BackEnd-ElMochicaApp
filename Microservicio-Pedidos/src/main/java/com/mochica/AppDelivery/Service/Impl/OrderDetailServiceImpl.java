package com.mochica.AppDelivery.Service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.mochica.AppDelivery.DTO.AddProductDTO;
import com.mochica.AppDelivery.DTO.DispositionDTO;
import com.mochica.AppDelivery.DTO.StockUpdateDTO;
import com.mochica.AppDelivery.Firebase.FBInitialize;
import com.mochica.AppDelivery.Service.OrderDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class OrderDetailServiceImpl implements OrderDetail {

    @Autowired
    private FBInitialize fbInitialize;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.product.url}")
    private String productoServiceUrl;

    @Value("${api.user.url}")
    private String userServiceUrl;

    private String collection = "orderdetails";

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


}
