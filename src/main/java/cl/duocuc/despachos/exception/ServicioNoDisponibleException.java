package cl.duocuc.despachos.exception;

/** Pedidos no respondio, por lo que no se puede validar el despacho. */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
