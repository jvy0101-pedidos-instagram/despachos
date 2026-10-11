package cl.duocuc.despachos.repository;

import cl.duocuc.despachos.model.Despacho;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    List<Despacho> findByPedidoId(Long pedidoId);
}
