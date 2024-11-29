package com.mochica.AppDelivery.Service.Impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mochica.AppDelivery.DTO.FormtokenResponseDTO;
import com.mochica.AppDelivery.DTO.InitiatePaymentDTO;
import com.mochica.AppDelivery.Entity.OrderDetail;
import com.mochica.AppDelivery.Entity.OrderStatus;
import com.mochica.AppDelivery.Firebase.FBInitialize;
import com.mochica.AppDelivery.Service.OrderDetailService;
import com.mochica.AppDelivery.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private FBInitialize fbInitialize;

    @Value("${izipay.public-key}")
    private String publicKey;

    @Value("${izipay.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://api.micuentaweb.pe/api-payment/V4/Charge/CreatePayment";
    private final String collection = "order";
    @Autowired
    private OrderDetailService orderDetailService; // Servicio para manejar órdenes

    @Override
    public FormtokenResponseDTO generateFormToken(InitiatePaymentDTO initiatePaymentDTO) throws Exception {

        CollectionReference ordersCollection = fbInitialize.getFirestore().collection(collection);
        CollectionReference usersCollection = fbInitialize.getFirestore().collection("users");

        // Obtener el email del usuario usando su userId
        DocumentSnapshot userDocument = usersCollection.document(initiatePaymentDTO.getUserId()).get().get();

        if (!userDocument.exists()) {
            throw new Exception("Usuario no encontrado en la base de datos.");
        }

        // Asumiendo que el campo del correo electrónico se llama "email" en el documento del usuario
        String email = userDocument.getString("email");


        // 1. Calcular el total del carrito
        List<OrderDetail> carrito = orderDetailService.obtenerCarrito(initiatePaymentDTO.getUserId());
        double totalAmount = carrito.stream().mapToDouble(OrderDetail::getPriceTotal).sum();

        // 2. Crear la orden
        Map<String, Object> docData = new HashMap<>();
        docData.put("UserId", initiatePaymentDTO.getUserId());
        docData.put("OrderDate", new Date());
        docData.put("Total", totalAmount);
        docData.put("OrderStatus", OrderStatus.PENDIENTE);
        docData.put("Details", carrito);
        // Agregar el documento en Firebase
        ApiFuture<WriteResult> writeResultApiFuture = ordersCollection.document().set(docData);
        writeResultApiFuture.get(); // Esperar hasta que se complete la actualización

        // 3. Generar el formToken en Izipay
        String auth = publicKey + ":" + secretKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("amount", (int) (totalAmount * 100)); // Izipay requiere el monto en centavos
        body.put("currency", initiatePaymentDTO.getCurrency()); // Cambia a la moneda deseada
        body.put("formAction", "PAYMENT");
        Map<String, Object> customer = new HashMap<>();
        customer.put("email", email);
        body.put("customer", customer);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("answer")) {
                    Map<String, Object> answer = (Map<String, Object>) responseBody.get("answer");
                    if (answer != null && answer.containsKey("formToken")) {
                        String formToken = (String) answer.get("formToken");
                        return new FormtokenResponseDTO(formToken);
                    }
                }
                throw new Exception("Respuesta de Izipay incompleta: " + responseBody);
            } else {
                System.err.println("Error en la solicitud a Izipay: " + response.getStatusCode());
                System.err.println("Respuesta: " + response.getBody());
                throw new Exception("Error al solicitar el formToken: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Excepción al conectar con Izipay: " + e.getMessage());
            throw e;
        }


    }
}
