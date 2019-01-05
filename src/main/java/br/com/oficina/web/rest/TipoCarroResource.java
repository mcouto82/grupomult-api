package br.com.oficina.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.oficina.domain.TipoCarro;
import br.com.oficina.service.TipoCarroService;
import br.com.oficina.web.rest.errors.BadRequestAlertException;
import br.com.oficina.web.rest.util.HeaderUtil;
import br.com.oficina.web.rest.util.PaginationUtil;
import br.com.oficina.service.dto.TipoCarroCriteria;
import br.com.oficina.service.TipoCarroQueryService;
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing TipoCarro.
 */
@RestController
@RequestMapping("/api")
public class TipoCarroResource {

    private final Logger log = LoggerFactory.getLogger(TipoCarroResource.class);

    private static final String ENTITY_NAME = "tipoCarro";

    private final TipoCarroService tipoCarroService;

    private final TipoCarroQueryService tipoCarroQueryService;

    public TipoCarroResource(TipoCarroService tipoCarroService, TipoCarroQueryService tipoCarroQueryService) {
        this.tipoCarroService = tipoCarroService;
        this.tipoCarroQueryService = tipoCarroQueryService;
    }

    /**
     * POST  /tipo-carros : Create a new tipoCarro.
     *
     * @param tipoCarro the tipoCarro to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tipoCarro, or with status 400 (Bad Request) if the tipoCarro has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tipo-carros")
    @Timed
    public ResponseEntity<TipoCarro> createTipoCarro(@RequestBody TipoCarro tipoCarro) throws URISyntaxException {
        log.debug("REST request to save TipoCarro : {}", tipoCarro);
        if (tipoCarro.getId() != null) {
            throw new BadRequestAlertException("A new tipoCarro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TipoCarro result = tipoCarroService.save(tipoCarro);
        return ResponseEntity.created(new URI("/api/tipo-carros/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tipo-carros : Updates an existing tipoCarro.
     *
     * @param tipoCarro the tipoCarro to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tipoCarro,
     * or with status 400 (Bad Request) if the tipoCarro is not valid,
     * or with status 500 (Internal Server Error) if the tipoCarro couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tipo-carros")
    @Timed
    public ResponseEntity<TipoCarro> updateTipoCarro(@RequestBody TipoCarro tipoCarro) throws URISyntaxException {
        log.debug("REST request to update TipoCarro : {}", tipoCarro);
        if (tipoCarro.getId() == null) {
            return createTipoCarro(tipoCarro);
        }
        TipoCarro result = tipoCarroService.save(tipoCarro);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tipoCarro.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tipo-carros : get all the tipoCarros.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tipoCarros in body
     */
    @GetMapping("/tipo-carros")
    @Timed
    public ResponseEntity<List<TipoCarro>> getAllTipoCarros(TipoCarroCriteria criteria,@ApiParam Pageable pageable) {
        log.debug("REST request to get TipoCarros by criteria: {}", criteria);
        Page<TipoCarro> page = tipoCarroQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipo-carros");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tipo-carros/:id : get the "id" tipoCarro.
     *
     * @param id the id of the tipoCarro to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tipoCarro, or with status 404 (Not Found)
     */
    @GetMapping("/tipo-carros/{id}")
    @Timed
    public ResponseEntity<TipoCarro> getTipoCarro(@PathVariable Long id) {
        log.debug("REST request to get TipoCarro : {}", id);
        TipoCarro tipoCarro = tipoCarroService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tipoCarro));
    }

    /**
     * DELETE  /tipo-carros/:id : delete the "id" tipoCarro.
     *
     * @param id the id of the tipoCarro to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tipo-carros/{id}")
    @Timed
    public ResponseEntity<Void> deleteTipoCarro(@PathVariable Long id) {
        log.debug("REST request to delete TipoCarro : {}", id);
        tipoCarroService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/tipo-carros?query=:query : search for the tipoCarro corresponding
     * to the query.
     *
     * @param query the query of the tipoCarro search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/tipo-carros")
    @Timed
    public ResponseEntity<List<TipoCarro>> searchTipoCarros(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of TipoCarros for query {}", query);
        Page<TipoCarro> page = tipoCarroService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tipo-carros");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
