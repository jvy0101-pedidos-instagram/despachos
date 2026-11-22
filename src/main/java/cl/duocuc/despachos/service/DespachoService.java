package cl.duocuc.despachos.service;

import cl.duocuc.despachos.client.PedidoClient;
import cl.duocuc.despachos.dto.PedidoResponse;
import cl.duocuc.despachos.exception.RecursoNoEncontradoException;
import cl.duocuc.despachos.exception.ReglaNegocioException;
import cl.duocuc.despachos.model.*;
import cl.duocuc.despachos.repository.DespachoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Capa de negocio: el controller llama al service y el service al repository. */
@Service
public class DespachoService {

    private final DespachoRepository repository;
    private final PedidoClient pedidoClient;

    // Inyeccion de dependencias por constructor.
    public DespachoService(DespachoRepository repository, PedidoClient pedidoClient) {
        this.repository = repository;
        this.pedidoClient = pedidoClient;
    }

    public List<Despacho> listar() {
        return repository.findAll();
    }

    public Despacho buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe despacho con id " + id));
    }

    public List<Despacho> listarPorPedidoId(Long pedidoId) {
        return repository.findByPedidoId(pedidoId);
    }

    @Transactional
    public Despacho crear(Despacho nuevo) {
        // 1. El pedido tiene que existir y estar pagado: no se despacha algo que no se ha pagado.
        PedidoResponse pedido = pedidoClient.buscarPedido(nuevo.getPedidoId());
        if (!"PAGADO".equals(pedido.estado())) {
            throw new ReglaNegocioException("El pedido " + pedido.id()
                    + " no se puede despachar porque esta " + pedido.estado() + "; primero debe estar PAGADO");
        }

        if (nuevo.getEstado() == null) nuevo.setEstado(EstadoDespacho.PREPARANDO);
        nuevo.setId(null); // el id lo genera la base de datos
        Despacho guardado = repository.save(nuevo);

        // 2. Se le avisa a Pedidos que el pedido ya salio a despacho.
        pedidoClient.cambiarEstado(guardado.getPedidoId(), "DESPACHADO");
        return guardado;
    }

    @Transactional
    public Despacho actualizar(Long id, Despacho datos) {
        Despacho existente = buscarPorId(id);
        // El pedido tiene que seguir existiendo en Pedidos.
        pedidoClient.buscarPedido(datos.getPedidoId());

        existente.setPedidoId(datos.getPedidoId());
        existente.setDireccion(datos.getDireccion());
        existente.setComuna(datos.getComuna());
        existente.setEmpresaTransporte(datos.getEmpresaTransporte());
        existente.setCodigoSeguimiento(datos.getCodigoSeguimiento());
        if (datos.getEstado() != null) existente.setEstado(datos.getEstado());
        Despacho guardado = repository.save(existente);

        // Cuando el despacho llega a ENTREGADO, el pedido tambien queda ENTREGADO.
        if (guardado.getEstado() == EstadoDespacho.ENTREGADO) {
            pedidoClient.cambiarEstado(guardado.getPedidoId(), "ENTREGADO");
        }
        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Despacho existente = buscarPorId(id);
        repository.delete(existente);
    }
}
