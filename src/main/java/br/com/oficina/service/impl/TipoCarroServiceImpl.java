package br.com.oficina.service.impl;

import br.com.oficina.service.TipoCarroService;
import br.com.oficina.domain.TipoCarro;
import br.com.oficina.repository.TipoCarroRepository;
import br.com.oficina.repository.search.TipoCarroSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing TipoCarro.
 */
@Service
@Transactional
public class TipoCarroServiceImpl implements TipoCarroService{

    private final Logger log = LoggerFactory.getLogger(TipoCarroServiceImpl.class);

    private final TipoCarroRepository tipoCarroRepository;

    private final TipoCarroSearchRepository tipoCarroSearchRepository;

    public TipoCarroServiceImpl(TipoCarroRepository tipoCarroRepository, TipoCarroSearchRepository tipoCarroSearchRepository) {
        this.tipoCarroRepository = tipoCarroRepository;
        this.tipoCarroSearchRepository = tipoCarroSearchRepository;
    }

    /**
     * Save a tipoCarro.
     *
     * @param tipoCarro the entity to save
     * @return the persisted entity
     */
    @Override
    public TipoCarro save(TipoCarro tipoCarro) {
        log.debug("Request to save TipoCarro : {}", tipoCarro);
        TipoCarro result = tipoCarroRepository.save(tipoCarro);
        tipoCarroSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the tipoCarros.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TipoCarro> findAll(Pageable pageable) {
        log.debug("Request to get all TipoCarros");
        return tipoCarroRepository.findAll(pageable);
    }

    /**
     *  Get one tipoCarro by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TipoCarro findOne(Long id) {
        log.debug("Request to get TipoCarro : {}", id);
        return tipoCarroRepository.findOne(id);
    }

    /**
     *  Delete the  tipoCarro by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TipoCarro : {}", id);
        tipoCarroRepository.delete(id);
        tipoCarroSearchRepository.delete(id);
    }

    /**
     * Search for the tipoCarro corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TipoCarro> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TipoCarros for query {}", query);
        Page<TipoCarro> result = tipoCarroSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
