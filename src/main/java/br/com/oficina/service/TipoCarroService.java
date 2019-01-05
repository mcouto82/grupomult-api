package br.com.oficina.service;

import br.com.oficina.domain.TipoCarro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing TipoCarro.
 */
public interface TipoCarroService {

    /**
     * Save a tipoCarro.
     *
     * @param tipoCarro the entity to save
     * @return the persisted entity
     */
    TipoCarro save(TipoCarro tipoCarro);

    /**
     *  Get all the tipoCarros.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<TipoCarro> findAll(Pageable pageable);

    /**
     *  Get the "id" tipoCarro.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    TipoCarro findOne(Long id);

    /**
     *  Delete the "id" tipoCarro.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the tipoCarro corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<TipoCarro> search(String query, Pageable pageable);
}
