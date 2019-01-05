package br.com.oficina.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import br.com.oficina.domain.TipoCarro;
import br.com.oficina.domain.*; // for static metamodels
import br.com.oficina.repository.TipoCarroRepository;
import br.com.oficina.repository.search.TipoCarroSearchRepository;
import br.com.oficina.service.dto.TipoCarroCriteria;


/**
 * Service for executing complex queries for TipoCarro entities in the database.
 * The main input is a {@link TipoCarroCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link TipoCarro} or a {@link Page} of {%link TipoCarro} which fulfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class TipoCarroQueryService extends QueryService<TipoCarro> {

    private final Logger log = LoggerFactory.getLogger(TipoCarroQueryService.class);


    private final TipoCarroRepository tipoCarroRepository;

    private final TipoCarroSearchRepository tipoCarroSearchRepository;

    public TipoCarroQueryService(TipoCarroRepository tipoCarroRepository, TipoCarroSearchRepository tipoCarroSearchRepository) {
        this.tipoCarroRepository = tipoCarroRepository;
        this.tipoCarroSearchRepository = tipoCarroSearchRepository;
    }

    /**
     * Return a {@link List} of {%link TipoCarro} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TipoCarro> findByCriteria(TipoCarroCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<TipoCarro> specification = createSpecification(criteria);
        return tipoCarroRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {%link TipoCarro} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TipoCarro> findByCriteria(TipoCarroCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<TipoCarro> specification = createSpecification(criteria);
        return tipoCarroRepository.findAll(specification, page);
    }

    /**
     * Function to convert TipoCarroCriteria to a {@link Specifications}
     */
    private Specifications<TipoCarro> createSpecification(TipoCarroCriteria criteria) {
        Specifications<TipoCarro> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), TipoCarro_.id));
            }
            if (criteria.getCodigo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCodigo(), TipoCarro_.codigo));
            }
            if (criteria.getDescricao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescricao(), TipoCarro_.descricao));
            }
        }
        return specification;
    }

}
