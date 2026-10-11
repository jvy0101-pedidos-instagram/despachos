package cl.duocuc.despachos.controller;

import cl.duocuc.despachos.model.Despacho;
import cl.duocuc.despachos.service.DespachoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Capa de presentacion: expone los endpoints REST del microservicio. */
@RestController
@RequestMapping("/api/v1/despachos")
public class DespachoController {

    private final DespachoService service;

    public DespachoController(DespachoService service) {
        this.service = service;
    }

    // GET /api/v1/despachos
    @GetMapping
    public List<Despacho> listar() {
        return service.listar();
    }

    // GET /api/v1/despachos/{id}
    @GetMapping("/{id}")
    public Despacho buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // GET /api/v1/despachos/pedido/{pedidoId}
    @GetMapping("/pedido/{pedidoId}")
    public List<Despacho> listarPorPedidoId(@PathVariable Long pedidoId) {
        return service.listarPorPedidoId(pedidoId);
    }

    // POST /api/v1/despachos  -> 201 Created
    @PostMapping
    public ResponseEntity<Despacho> crear(@Valid @RequestBody Despacho despacho) {
        Despacho creado = service.crear(despacho);
        return ResponseEntity.created(URI.create("/api/v1/despachos/" + creado.getId())).body(creado);
    }

    // PUT /api/v1/despachos/{id}
    @PutMapping("/{id}")
    public Despacho actualizar(@PathVariable Long id, @Valid @RequestBody Despacho despacho) {
        return service.actualizar(id, despacho);
    }

    // DELETE /api/v1/despachos/{id}  -> 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
