package br.com.oficina.repository;

import br.com.oficina.domain.TipoCarro;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the TipoCarro entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TipoCarroRepository extends JpaRepository<TipoCarro, Long>, JpaSpecificationExecutor<TipoCarro> {

}
