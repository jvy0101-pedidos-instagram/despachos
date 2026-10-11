package cl.duocuc.despachos.service;

import cl.duocuc.despachos.exception.RecursoNoEncontradoException;
import cl.duocuc.despachos.model.*;
import cl.duocuc.despachos.repository.DespachoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Capa de negocio: el controller llama al service y el service al repository. */
@Service
public class DespachoService {

    private final DespachoRepository repository;

    // Inyeccion de dependencias por constructor.
    public DespachoService(DespachoRepository repository) {
        this.repository = repository;
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
        if (nuevo.getEstado() == null) nuevo.setEstado(EstadoDespacho.PREPARANDO);
        nuevo.setId(null); // el id lo genera la base de datos
        return repository.save(nuevo);
    }

    @Transactional
    public Despacho actualizar(Long id, Despacho datos) {
        Despacho existente = buscarPorId(id);
        existente.setPedidoId(datos.getPedidoId());
        existente.setDireccion(datos.getDireccion());
        existente.setComuna(datos.getComuna());
        existente.setEmpresaTransporte(datos.getEmpresaTransporte());
        existente.setCodigoSeguimiento(datos.getCodigoSeguimiento());
        if (datos.getEstado() != null) existente.setEstado(datos.getEstado());
        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Despacho existente = buscarPorId(id);
        repository.delete(existente);
    }
}
