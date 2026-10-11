package cl.duocuc.despachos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "despachos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El pedidoId es obligatorio")
    @Column(nullable = false)
    private Long pedidoId;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false, length = 200)
    private String direccion;

    @NotBlank(message = "La comuna es obligatoria")
    @Column(nullable = false, length = 60)
    private String comuna;

    @Column(length = 60)
    private String empresaTransporte;

    @Column(length = 60)
    private String codigoSeguimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDespacho estado = EstadoDespacho.PREPARANDO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /** Se ejecuta antes del INSERT: registra la fecha automaticamente. */
    @PrePersist
    void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
