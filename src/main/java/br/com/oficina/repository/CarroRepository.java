package br.com.oficina.repository;

import br.com.oficina.domain.Carro;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Carro entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarroRepository extends JpaRepository<Carro, Long>, JpaSpecificationExecutor<Carro> {

}
