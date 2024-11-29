package com.mochica.AppDelivery.Service.Impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mochica.AppDelivery.DTO.FormtokenResponseDTO;
import com.mochica.AppDelivery.DTO.InitiatePaymentDTO;
import com.mochica.AppDelivery.DTO.UpdateStatusDTO;
import com.mochica.AppDelivery.Entity.Order;
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
import java.util.concurrent.ExecutionException;

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
        docData.put("StatusCounter", false);
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
        body.put("paymentMode", "CPT");
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


    @Override
    public List<Order> obtenerOrder(String userId) throws ExecutionException, InterruptedException {
        // Obtener la colección de pedidos desde Firestore
        CollectionReference ordersCollection = fbInitialize.getFirestore().collection(collection);

        // Hacer una consulta para obtener todos los pedidos relacionados con el UserId
        ApiFuture<QuerySnapshot> querySnapshotFuture = ordersCollection.whereEqualTo("UserId", userId).get();
        List<QueryDocumentSnapshot> orderDocuments = querySnapshotFuture.get().getDocuments();

        // Crear una lista para almacenar los pedidos
        List<Order> orderList = new ArrayList<>();

        // Iterar sobre los documentos obtenidos
        for (QueryDocumentSnapshot document : orderDocuments) {
            Order order = document.toObject(Order.class);

            order.setId(document.getId());

            orderList.add(order);
        }

        // Devolver la lista de pedidos
        return orderList;
    }

    @Override
    public Boolean actualizarStatusCounter(String orderId) {
        CollectionReference ordersCollection = fbInitialize.getFirestore().collection(collection);

        try {
            // Obtener el documento específico usando el orderId
            DocumentReference orderDocRef = ordersCollection.document(orderId);

            // Obtener el documento
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = orderDocRef.get();
            DocumentSnapshot documentSnapshot = documentSnapshotFuture.get();

            if (documentSnapshot.exists()) {
                // Si el documento existe, obtener el valor actual de 'statusCounter'
                Boolean currentStatusCounter = documentSnapshot.getBoolean("statusCounter");

                if (currentStatusCounter != null && !currentStatusCounter) {
                    // Si 'statusCounter' es false, actualizamos el valor
                    orderDocRef.update("statusCounter", true).get(); // Actualizamos el campo 'statusCounter' a 'true'

                    // Si necesitas hacer algo con el documento o sus detalles, puedes hacerlo aquí
                    return true;
                } else {
                    return false;
                }
            } else {
                // Si el documento no existe
                System.out.println("No se encontró el documento con el ID: " + orderId);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean actualizarStatus(String orderId, UpdateStatusDTO updateStatusDTO) {
        // Obtén la referencia a la colección de pedidos
        CollectionReference ordersCollection = fbInitialize.getFirestore().collection(collection);

        try {
            // Obtener el documento específico usando el orderId
            DocumentReference orderDocRef = ordersCollection.document(orderId);

            // Obtener el documento
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = orderDocRef.get();
            DocumentSnapshot documentSnapshot = documentSnapshotFuture.get();

            if (documentSnapshot.exists()) {
                // Si el documento existe, actualizamos el campo 'orderStatus' con el valor del enum
                orderDocRef.update("OrderStatus", updateStatusDTO.getNewStatus()).get();  // Usamos .name() para obtener el nombre del enum como String

                // Confirmamos la actualización
                System.out.println("OrderStatus actualizado a: " + updateStatusDTO.getNewStatus());
                return true;
            } else {
                // Si el documento no existe
                System.out.println("No se encontró el documento con el ID: " + orderId);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean eliminarCarrito(String userId) {
        CollectionReference carritosCollection = fbInitialize.getFirestore().collection("orderdetails");

        try {
            // Filtrar los documentos por userId
            Query query = carritosCollection.whereEqualTo("UserId", userId);
            ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();

            // Esperamos a obtener los documentos
            QuerySnapshot querySnapshot = querySnapshotFuture.get();

            // Si encontramos documentos que coinciden con el userId
            if (!querySnapshot.isEmpty()) {
                // Eliminar todos los documentos encontrados
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    DocumentReference documentRef = carritosCollection.document(documentSnapshot.getId());
                    ApiFuture<WriteResult> deleteFuture = documentRef.delete(); // Cambiar a ApiFuture<WriteResult>
                    deleteFuture.get();  // Esperamos a que se elimine el documento
                    System.out.println("Carrito con ID: " + documentSnapshot.getId() + " eliminado.");
                }
                return true;  // Indicamos que la eliminación fue exitosa
            } else {
                // No se encontraron documentos con el userId proporcionado
                System.out.println("No se encontraron carritos para el userId: " + userId);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // En caso de error
        }
    }
}
