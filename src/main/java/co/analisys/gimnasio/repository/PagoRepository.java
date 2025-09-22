package co.analisys.gimnasio.repository;

import co.analisys.gimnasio.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    List<Pago> findByMiembroId(Long miembroId);
    
    List<Pago> findByEstado(Pago.EstadoPago estado);
    
    Optional<Pago> findByTransactionId(String transactionId);
    
    @Query("SELECT p FROM Pago p WHERE p.miembroId = :miembroId AND p.estado = :estado")
    List<Pago> findByMiembroIdAndEstado(@Param("miembroId") Long miembroId, 
                                       @Param("estado") Pago.EstadoPago estado);
    
    @Query("SELECT p FROM Pago p WHERE p.estado = 'FALLIDO' AND p.intentos < :maxIntentos")
    List<Pago> findPagosParaReintentar(@Param("maxIntentos") Integer maxIntentos);
}