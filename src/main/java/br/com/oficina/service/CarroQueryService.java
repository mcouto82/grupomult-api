package br.com.oficina.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import br.com.oficina.domain.Carro;
import br.com.oficina.domain.*; // for static metamodels
import br.com.oficina.repository.CarroRepository;
import br.com.oficina.repository.search.CarroSearchRepository;
import br.com.oficina.service.dto.CarroCriteria;


/**
 * Service for executing complex queries for Carro entities in the database.
 * The main input is a {@link CarroCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link Carro} or a {@link Page} of {%link Carro} which fulfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class CarroQueryService extends QueryService<Carro> {

    private final Logger log = LoggerFactory.getLogger(CarroQueryService.class);


    private final CarroRepository carroRepository;

    private final CarroSearchRepository carroSearchRepository;

    public CarroQueryService(CarroRepository carroRepository, CarroSearchRepository carroSearchRepository) {
        this.carroRepository = carroRepository;
        this.carroSearchRepository = carroSearchRepository;
    }

    /**
     * Return a {@link List} of {%link Carro} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Carro> findByCriteria(CarroCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Carro> specification = createSpecification(criteria);
        return carroRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {%link Carro} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Carro> findByCriteria(CarroCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Carro> specification = createSpecification(criteria);
        return carroRepository.findAll(specification, page);
    }

    /**
     * Function to convert CarroCriteria to a {@link Specifications}
     */
    private Specifications<Carro> createSpecification(CarroCriteria criteria) {
        Specifications<Carro> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Carro_.id));
            }
            if (criteria.getCodigo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCodigo(), Carro_.codigo));
            }
            if (criteria.getDescricao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescricao(), Carro_.descricao));
            }
            if (criteria.getDataCriacao() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataCriacao(), Carro_.dataCriacao));
            }
            if (criteria.getDataAtualizacao() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataAtualizacao(), Carro_.dataAtualizacao));
            }
            if (criteria.getTipoCarroId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getTipoCarroId(), Carro_.tipoCarro, TipoCarro_.id));
            }
        }
        return specification;
    }

}
