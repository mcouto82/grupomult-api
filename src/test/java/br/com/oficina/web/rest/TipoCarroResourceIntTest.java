package br.com.oficina.web.rest;

import br.com.oficina.OficinaApp;

import br.com.oficina.domain.TipoCarro;
import br.com.oficina.repository.TipoCarroRepository;
import br.com.oficina.service.TipoCarroService;
import br.com.oficina.repository.search.TipoCarroSearchRepository;
import br.com.oficina.web.rest.errors.ExceptionTranslator;
import br.com.oficina.service.dto.TipoCarroCriteria;
import br.com.oficina.service.TipoCarroQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static br.com.oficina.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TipoCarroResource REST controller.
 *
 * @see TipoCarroResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OficinaApp.class)
public class TipoCarroResourceIntTest {

    private static final Long DEFAULT_CODIGO = 1L;
    private static final Long UPDATED_CODIGO = 2L;

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    @Autowired
    private TipoCarroRepository tipoCarroRepository;

    @Autowired
    private TipoCarroService tipoCarroService;

    @Autowired
    private TipoCarroSearchRepository tipoCarroSearchRepository;

    @Autowired
    private TipoCarroQueryService tipoCarroQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTipoCarroMockMvc;

    private TipoCarro tipoCarro;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TipoCarroResource tipoCarroResource = new TipoCarroResource(tipoCarroService, tipoCarroQueryService);
        this.restTipoCarroMockMvc = MockMvcBuilders.standaloneSetup(tipoCarroResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoCarro createEntity(EntityManager em) {
        TipoCarro tipoCarro = new TipoCarro()
            .codigo(DEFAULT_CODIGO)
            .descricao(DEFAULT_DESCRICAO);
        return tipoCarro;
    }

    @Before
    public void initTest() {
        tipoCarroSearchRepository.deleteAll();
        tipoCarro = createEntity(em);
    }

    @Test
    @Transactional
    public void createTipoCarro() throws Exception {
        int databaseSizeBeforeCreate = tipoCarroRepository.findAll().size();

        // Create the TipoCarro
        restTipoCarroMockMvc.perform(post("/api/tipo-carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tipoCarro)))
            .andExpect(status().isCreated());

        // Validate the TipoCarro in the database
        List<TipoCarro> tipoCarroList = tipoCarroRepository.findAll();
        assertThat(tipoCarroList).hasSize(databaseSizeBeforeCreate + 1);
        TipoCarro testTipoCarro = tipoCarroList.get(tipoCarroList.size() - 1);
        assertThat(testTipoCarro.getCodigo()).isEqualTo(DEFAULT_CODIGO);
        assertThat(testTipoCarro.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);

        // Validate the TipoCarro in Elasticsearch
        TipoCarro tipoCarroEs = tipoCarroSearchRepository.findOne(testTipoCarro.getId());
        assertThat(tipoCarroEs).isEqualToComparingFieldByField(testTipoCarro);
    }

    @Test
    @Transactional
    public void createTipoCarroWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tipoCarroRepository.findAll().size();

        // Create the TipoCarro with an existing ID
        tipoCarro.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTipoCarroMockMvc.perform(post("/api/tipo-carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tipoCarro)))
            .andExpect(status().isBadRequest());

        // Validate the TipoCarro in the database
        List<TipoCarro> tipoCarroList = tipoCarroRepository.findAll();
        assertThat(tipoCarroList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTipoCarros() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList
        restTipoCarroMockMvc.perform(get("/api/tipo-carros?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipoCarro.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void getTipoCarro() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get the tipoCarro
        restTipoCarroMockMvc.perform(get("/api/tipo-carros/{id}", tipoCarro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tipoCarro.getId().intValue()))
            .andExpect(jsonPath("$.codigo").value(DEFAULT_CODIGO.intValue()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()));
    }

    @Test
    @Transactional
    public void getAllTipoCarrosByCodigoIsEqualToSomething() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where codigo equals to DEFAULT_CODIGO
        defaultTipoCarroShouldBeFound("codigo.equals=" + DEFAULT_CODIGO);

        // Get all the tipoCarroList where codigo equals to UPDATED_CODIGO
        defaultTipoCarroShouldNotBeFound("codigo.equals=" + UPDATED_CODIGO);
    }

    @Test
    @Transactional
    public void getAllTipoCarrosByCodigoIsInShouldWork() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where codigo in DEFAULT_CODIGO or UPDATED_CODIGO
        defaultTipoCarroShouldBeFound("codigo.in=" + DEFAULT_CODIGO + "," + UPDATED_CODIGO);

        // Get all the tipoCarroList where codigo equals to UPDATED_CODIGO
        defaultTipoCarroShouldNotBeFound("codigo.in=" + UPDATED_CODIGO);
    }

    @Test
    @Transactional
    public void getAllTipoCarrosByCodigoIsNullOrNotNull() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where codigo is not null
        defaultTipoCarroShouldBeFound("codigo.specified=true");

        // Get all the tipoCarroList where codigo is null
        defaultTipoCarroShouldNotBeFound("codigo.specified=false");
    }

    @Test
    @Transactional
    public void getAllTipoCarrosByCodigoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where codigo greater than or equals to DEFAULT_CODIGO
        defaultTipoCarroShouldBeFound("codigo.greaterOrEqualThan=" + DEFAULT_CODIGO);

        // Get all the tipoCarroList where codigo greater than or equals to UPDATED_CODIGO
        defaultTipoCarroShouldNotBeFound("codigo.greaterOrEqualThan=" + UPDATED_CODIGO);
    }

    @Test
    @Transactional
    public void getAllTipoCarrosByCodigoIsLessThanSomething() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where codigo less than or equals to DEFAULT_CODIGO
        defaultTipoCarroShouldNotBeFound("codigo.lessThan=" + DEFAULT_CODIGO);

        // Get all the tipoCarroList where codigo less than or equals to UPDATED_CODIGO
        defaultTipoCarroShouldBeFound("codigo.lessThan=" + UPDATED_CODIGO);
    }


    @Test
    @Transactional
    public void getAllTipoCarrosByDescricaoIsEqualToSomething() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where descricao equals to DEFAULT_DESCRICAO
        defaultTipoCarroShouldBeFound("descricao.equals=" + DEFAULT_DESCRICAO);

        // Get all the tipoCarroList where descricao equals to UPDATED_DESCRICAO
        defaultTipoCarroShouldNotBeFound("descricao.equals=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    public void getAllTipoCarrosByDescricaoIsInShouldWork() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where descricao in DEFAULT_DESCRICAO or UPDATED_DESCRICAO
        defaultTipoCarroShouldBeFound("descricao.in=" + DEFAULT_DESCRICAO + "," + UPDATED_DESCRICAO);

        // Get all the tipoCarroList where descricao equals to UPDATED_DESCRICAO
        defaultTipoCarroShouldNotBeFound("descricao.in=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    public void getAllTipoCarrosByDescricaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        tipoCarroRepository.saveAndFlush(tipoCarro);

        // Get all the tipoCarroList where descricao is not null
        defaultTipoCarroShouldBeFound("descricao.specified=true");

        // Get all the tipoCarroList where descricao is null
        defaultTipoCarroShouldNotBeFound("descricao.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTipoCarroShouldBeFound(String filter) throws Exception {
        restTipoCarroMockMvc.perform(get("/api/tipo-carros?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipoCarro.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTipoCarroShouldNotBeFound(String filter) throws Exception {
        restTipoCarroMockMvc.perform(get("/api/tipo-carros?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingTipoCarro() throws Exception {
        // Get the tipoCarro
        restTipoCarroMockMvc.perform(get("/api/tipo-carros/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTipoCarro() throws Exception {
        // Initialize the database
        tipoCarroService.save(tipoCarro);

        int databaseSizeBeforeUpdate = tipoCarroRepository.findAll().size();

        // Update the tipoCarro
        TipoCarro updatedTipoCarro = tipoCarroRepository.findOne(tipoCarro.getId());
        updatedTipoCarro
            .codigo(UPDATED_CODIGO)
            .descricao(UPDATED_DESCRICAO);

        restTipoCarroMockMvc.perform(put("/api/tipo-carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTipoCarro)))
            .andExpect(status().isOk());

        // Validate the TipoCarro in the database
        List<TipoCarro> tipoCarroList = tipoCarroRepository.findAll();
        assertThat(tipoCarroList).hasSize(databaseSizeBeforeUpdate);
        TipoCarro testTipoCarro = tipoCarroList.get(tipoCarroList.size() - 1);
        assertThat(testTipoCarro.getCodigo()).isEqualTo(UPDATED_CODIGO);
        assertThat(testTipoCarro.getDescricao()).isEqualTo(UPDATED_DESCRICAO);

        // Validate the TipoCarro in Elasticsearch
        TipoCarro tipoCarroEs = tipoCarroSearchRepository.findOne(testTipoCarro.getId());
        assertThat(tipoCarroEs).isEqualToComparingFieldByField(testTipoCarro);
    }

    @Test
    @Transactional
    public void updateNonExistingTipoCarro() throws Exception {
        int databaseSizeBeforeUpdate = tipoCarroRepository.findAll().size();

        // Create the TipoCarro

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTipoCarroMockMvc.perform(put("/api/tipo-carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tipoCarro)))
            .andExpect(status().isCreated());

        // Validate the TipoCarro in the database
        List<TipoCarro> tipoCarroList = tipoCarroRepository.findAll();
        assertThat(tipoCarroList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTipoCarro() throws Exception {
        // Initialize the database
        tipoCarroService.save(tipoCarro);

        int databaseSizeBeforeDelete = tipoCarroRepository.findAll().size();

        // Get the tipoCarro
        restTipoCarroMockMvc.perform(delete("/api/tipo-carros/{id}", tipoCarro.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean tipoCarroExistsInEs = tipoCarroSearchRepository.exists(tipoCarro.getId());
        assertThat(tipoCarroExistsInEs).isFalse();

        // Validate the database is empty
        List<TipoCarro> tipoCarroList = tipoCarroRepository.findAll();
        assertThat(tipoCarroList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTipoCarro() throws Exception {
        // Initialize the database
        tipoCarroService.save(tipoCarro);

        // Search the tipoCarro
        restTipoCarroMockMvc.perform(get("/api/_search/tipo-carros?query=id:" + tipoCarro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipoCarro.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoCarro.class);
        TipoCarro tipoCarro1 = new TipoCarro();
        tipoCarro1.setId(1L);
        TipoCarro tipoCarro2 = new TipoCarro();
        tipoCarro2.setId(tipoCarro1.getId());
        assertThat(tipoCarro1).isEqualTo(tipoCarro2);
        tipoCarro2.setId(2L);
        assertThat(tipoCarro1).isNotEqualTo(tipoCarro2);
        tipoCarro1.setId(null);
        assertThat(tipoCarro1).isNotEqualTo(tipoCarro2);
    }
}
