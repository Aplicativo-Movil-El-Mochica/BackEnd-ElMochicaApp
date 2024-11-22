package org.mochica.AppDelivery.DTO;

import lombok.Data;
import org.mochica.AppDelivery.Entity.PedidoItem;

import java.util.List;

@Data
public class EmailDTO {
    String destinatario;
    String nombre;
    String cc;
    String tipocc;
    String dni;
    String tipoPedido;
    String Direccion;
    String FechaPedido;
    private List<PedidoItem> resumenPedido;
    double  total;


}
