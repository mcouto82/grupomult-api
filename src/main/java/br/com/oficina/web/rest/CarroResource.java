package br.com.oficina.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.oficina.domain.Carro;
import br.com.oficina.service.CarroService;
import br.com.oficina.web.rest.errors.BadRequestAlertException;
import br.com.oficina.web.rest.util.HeaderUtil;
import br.com.oficina.web.rest.util.PaginationUtil;
import br.com.oficina.service.dto.CarroCriteria;
import br.com.oficina.service.CarroQueryService;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Carro.
 */
@RestController
@RequestMapping("/api")
public class CarroResource {

    private final Logger log = LoggerFactory.getLogger(CarroResource.class);

    private static final String ENTITY_NAME = "carro";

    private final CarroService carroService;

    private final CarroQueryService carroQueryService;

    public CarroResource(CarroService carroService, CarroQueryService carroQueryService) {
        this.carroService = carroService;
        this.carroQueryService = carroQueryService;
    }

    /**
     * POST  /carros : Create a new carro.
     *
     * @param carro the carro to create
     * @return the ResponseEntity with status 201 (Created) and with body the new carro, or with status 400 (Bad Request) if the carro has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/carros")
    @Timed
    public ResponseEntity<Carro> createCarro(@Valid @RequestBody Carro carro) throws URISyntaxException {
        log.debug("REST request to save Carro : {}", carro);
        if (carro.getId() != null) {
            throw new BadRequestAlertException("A new carro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Carro result = carroService.save(carro);
        return ResponseEntity.created(new URI("/api/carros/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /carros : Updates an existing carro.
     *
     * @param carro the carro to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated carro,
     * or with status 400 (Bad Request) if the carro is not valid,
     * or with status 500 (Internal Server Error) if the carro couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/carros")
    @Timed
    public ResponseEntity<Carro> updateCarro(@Valid @RequestBody Carro carro) throws URISyntaxException {
        log.debug("REST request to update Carro : {}", carro);
        if (carro.getId() == null) {
            return createCarro(carro);
        }
        Carro result = carroService.save(carro);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, carro.getId().toString()))
            .body(result);
    }

    /**
     * GET  /carros : get all the carros.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of carros in body
     */
    @GetMapping("/carros")
    @Timed
    public ResponseEntity<List<Carro>> getAllCarros(CarroCriteria criteria,@ApiParam Pageable pageable) {
        log.debug("REST request to get Carros by criteria: {}", criteria);
        Page<Carro> page = carroQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/carros");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /carros/:id : get the "id" carro.
     *
     * @param id the id of the carro to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the carro, or with status 404 (Not Found)
     */
    @GetMapping("/carros/{id}")
    @Timed
    public ResponseEntity<Carro> getCarro(@PathVariable Long id) {
        log.debug("REST request to get Carro : {}", id);
        Carro carro = carroService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(carro));
    }

    /**
     * DELETE  /carros/:id : delete the "id" carro.
     *
     * @param id the id of the carro to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/carros/{id}")
    @Timed
    public ResponseEntity<Void> deleteCarro(@PathVariable Long id) {
        log.debug("REST request to delete Carro : {}", id);
        carroService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/carros?query=:query : search for the carro corresponding
     * to the query.
     *
     * @param query the query of the carro search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/carros")
    @Timed
    public ResponseEntity<List<Carro>> searchCarros(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Carros for query {}", query);
        Page<Carro> page = carroService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/carros");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
