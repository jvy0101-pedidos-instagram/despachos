package cl.duocuc.despachos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duocuc.despachos.client.PedidoClient;
import cl.duocuc.despachos.dto.PedidoResponse;
import cl.duocuc.despachos.exception.ReferenciaInvalidaException;
import cl.duocuc.despachos.exception.ReglaNegocioException;
import cl.duocuc.despachos.model.Despacho;
import cl.duocuc.despachos.model.EstadoDespacho;
import cl.duocuc.despachos.repository.DespachoRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de las reglas de negocio de Despachos.
 * El microservicio Pedidos y la base de datos se reemplazan por mocks (objetos simulados).
 */
@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {

    @Mock
    private DespachoRepository repository;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private DespachoService service;

    private Despacho despacho() {
        Despacho d = new Despacho();
        d.setPedidoId(1L);
        d.setDireccion("Av. Siempre Viva 123");
        d.setComuna("Santiago");
        return d;
    }

    @Test
    void crearDespachaUnPedidoPagadoYLoMarcaDespachado() {
        when(pedidoClient.buscarPedido(1L))
                .thenReturn(new PedidoResponse(1L, new BigDecimal("25980.00"), "PAGADO"));
        when(repository.save(any(Despacho.class))).thenAnswer(inv -> inv.getArgument(0));

        Despacho creado = service.crear(despacho());

        assertThat(creado.getEstado()).isEqualTo(EstadoDespacho.PREPARANDO);
        verify(pedidoClient).cambiarEstado(1L, "DESPACHADO");
    }

    @Test
    void crearRechazaUnPedidoQueNoEstaPagado() {
        when(pedidoClient.buscarPedido(1L))
                .thenReturn(new PedidoResponse(1L, new BigDecimal("25980.00"), "PENDIENTE"));

        assertThatThrownBy(() -> service.crear(despacho()))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("PAGADO");

        verify(repository, never()).save(any());
        verify(pedidoClient, never()).cambiarEstado(any(), anyString());
    }

    @Test
    void crearRechazaUnPedidoQueNoExiste() {
        when(pedidoClient.buscarPedido(1L))
                .thenThrow(new ReferenciaInvalidaException("No existe pedido con id 1"));

        assertThatThrownBy(() -> service.crear(despacho()))
                .isInstanceOf(ReferenciaInvalidaException.class);

        verify(repository, never()).save(any());
    }
}
