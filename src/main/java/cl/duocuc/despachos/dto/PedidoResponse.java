package cl.duocuc.despachos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Los datos de un pedido que Despachos necesita, tal como los responde el microservicio Pedidos.
 * ignoreUnknown descarta los campos que Despachos no usa (detalles, fecha...).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PedidoResponse(
        Long id,
        BigDecimal total,
        String estado) {
}
