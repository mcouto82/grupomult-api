package br.com.oficina.web.rest;

import br.com.oficina.OficinaApp;

import br.com.oficina.domain.Carro;
import br.com.oficina.domain.TipoCarro;
import br.com.oficina.repository.CarroRepository;
import br.com.oficina.service.CarroService;
import br.com.oficina.repository.search.CarroSearchRepository;
import br.com.oficina.web.rest.errors.ExceptionTranslator;
import br.com.oficina.service.dto.CarroCriteria;
import br.com.oficina.service.CarroQueryService;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static br.com.oficina.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CarroResource REST controller.
 *
 * @see CarroResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OficinaApp.class)
public class CarroResourceIntTest {

    private static final Long DEFAULT_CODIGO = 1L;
    private static final Long UPDATED_CODIGO = 2L;

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_CRIACAO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_CRIACAO = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATA_ATUALIZACAO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_ATUALIZACAO = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private CarroService carroService;

    @Autowired
    private CarroSearchRepository carroSearchRepository;

    @Autowired
    private CarroQueryService carroQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCarroMockMvc;

    private Carro carro;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CarroResource carroResource = new CarroResource(carroService, carroQueryService);
        this.restCarroMockMvc = MockMvcBuilders.standaloneSetup(carroResource)
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
    public static Carro createEntity(EntityManager em) {
        Carro carro = new Carro()
            .codigo(DEFAULT_CODIGO)
            .descricao(DEFAULT_DESCRICAO)
            .dataCriacao(DEFAULT_DATA_CRIACAO)
            .dataAtualizacao(DEFAULT_DATA_ATUALIZACAO);
        // Add required entity
        TipoCarro tipoCarro = TipoCarroResourceIntTest.createEntity(em);
        em.persist(tipoCarro);
        em.flush();
        carro.setTipoCarro(tipoCarro);
        return carro;
    }

    @Before
    public void initTest() {
        carroSearchRepository.deleteAll();
        carro = createEntity(em);
    }

    @Test
    @Transactional
    public void createCarro() throws Exception {
        int databaseSizeBeforeCreate = carroRepository.findAll().size();

        // Create the Carro
        restCarroMockMvc.perform(post("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isCreated());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeCreate + 1);
        Carro testCarro = carroList.get(carroList.size() - 1);
        assertThat(testCarro.getCodigo()).isEqualTo(DEFAULT_CODIGO);
        assertThat(testCarro.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCarro.getDataCriacao()).isEqualTo(DEFAULT_DATA_CRIACAO);
        assertThat(testCarro.getDataAtualizacao()).isEqualTo(DEFAULT_DATA_ATUALIZACAO);

        // Validate the Carro in Elasticsearch
        Carro carroEs = carroSearchRepository.findOne(testCarro.getId());
        assertThat(carroEs).isEqualToComparingFieldByField(testCarro);
    }

    @Test
    @Transactional
    public void createCarroWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = carroRepository.findAll().size();

        // Create the Carro with an existing ID
        carro.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarroMockMvc.perform(post("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isBadRequest());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCarros() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList
        restCarroMockMvc.perform(get("/api/carros?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carro.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())))
            .andExpect(jsonPath("$.[*].dataCriacao").value(hasItem(DEFAULT_DATA_CRIACAO.toString())))
            .andExpect(jsonPath("$.[*].dataAtualizacao").value(hasItem(DEFAULT_DATA_ATUALIZACAO.toString())));
    }

    @Test
    @Transactional
    public void getCarro() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get the carro
        restCarroMockMvc.perform(get("/api/carros/{id}", carro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(carro.getId().intValue()))
            .andExpect(jsonPath("$.codigo").value(DEFAULT_CODIGO.intValue()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()))
            .andExpect(jsonPath("$.dataCriacao").value(DEFAULT_DATA_CRIACAO.toString()))
            .andExpect(jsonPath("$.dataAtualizacao").value(DEFAULT_DATA_ATUALIZACAO.toString()));
    }

    @Test
    @Transactional
    public void getAllCarrosByCodigoIsEqualToSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where codigo equals to DEFAULT_CODIGO
        defaultCarroShouldBeFound("codigo.equals=" + DEFAULT_CODIGO);

        // Get all the carroList where codigo equals to UPDATED_CODIGO
        defaultCarroShouldNotBeFound("codigo.equals=" + UPDATED_CODIGO);
    }

    @Test
    @Transactional
    public void getAllCarrosByCodigoIsInShouldWork() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where codigo in DEFAULT_CODIGO or UPDATED_CODIGO
        defaultCarroShouldBeFound("codigo.in=" + DEFAULT_CODIGO + "," + UPDATED_CODIGO);

        // Get all the carroList where codigo equals to UPDATED_CODIGO
        defaultCarroShouldNotBeFound("codigo.in=" + UPDATED_CODIGO);
    }

    @Test
    @Transactional
    public void getAllCarrosByCodigoIsNullOrNotNull() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where codigo is not null
        defaultCarroShouldBeFound("codigo.specified=true");

        // Get all the carroList where codigo is null
        defaultCarroShouldNotBeFound("codigo.specified=false");
    }

    @Test
    @Transactional
    public void getAllCarrosByCodigoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where codigo greater than or equals to DEFAULT_CODIGO
        defaultCarroShouldBeFound("codigo.greaterOrEqualThan=" + DEFAULT_CODIGO);

        // Get all the carroList where codigo greater than or equals to UPDATED_CODIGO
        defaultCarroShouldNotBeFound("codigo.greaterOrEqualThan=" + UPDATED_CODIGO);
    }

    @Test
    @Transactional
    public void getAllCarrosByCodigoIsLessThanSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where codigo less than or equals to DEFAULT_CODIGO
        defaultCarroShouldNotBeFound("codigo.lessThan=" + DEFAULT_CODIGO);

        // Get all the carroList where codigo less than or equals to UPDATED_CODIGO
        defaultCarroShouldBeFound("codigo.lessThan=" + UPDATED_CODIGO);
    }


    @Test
    @Transactional
    public void getAllCarrosByDescricaoIsEqualToSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where descricao equals to DEFAULT_DESCRICAO
        defaultCarroShouldBeFound("descricao.equals=" + DEFAULT_DESCRICAO);

        // Get all the carroList where descricao equals to UPDATED_DESCRICAO
        defaultCarroShouldNotBeFound("descricao.equals=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDescricaoIsInShouldWork() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where descricao in DEFAULT_DESCRICAO or UPDATED_DESCRICAO
        defaultCarroShouldBeFound("descricao.in=" + DEFAULT_DESCRICAO + "," + UPDATED_DESCRICAO);

        // Get all the carroList where descricao equals to UPDATED_DESCRICAO
        defaultCarroShouldNotBeFound("descricao.in=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDescricaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where descricao is not null
        defaultCarroShouldBeFound("descricao.specified=true");

        // Get all the carroList where descricao is null
        defaultCarroShouldNotBeFound("descricao.specified=false");
    }

    @Test
    @Transactional
    public void getAllCarrosByDataCriacaoIsEqualToSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataCriacao equals to DEFAULT_DATA_CRIACAO
        defaultCarroShouldBeFound("dataCriacao.equals=" + DEFAULT_DATA_CRIACAO);

        // Get all the carroList where dataCriacao equals to UPDATED_DATA_CRIACAO
        defaultCarroShouldNotBeFound("dataCriacao.equals=" + UPDATED_DATA_CRIACAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDataCriacaoIsInShouldWork() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataCriacao in DEFAULT_DATA_CRIACAO or UPDATED_DATA_CRIACAO
        defaultCarroShouldBeFound("dataCriacao.in=" + DEFAULT_DATA_CRIACAO + "," + UPDATED_DATA_CRIACAO);

        // Get all the carroList where dataCriacao equals to UPDATED_DATA_CRIACAO
        defaultCarroShouldNotBeFound("dataCriacao.in=" + UPDATED_DATA_CRIACAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDataCriacaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataCriacao is not null
        defaultCarroShouldBeFound("dataCriacao.specified=true");

        // Get all the carroList where dataCriacao is null
        defaultCarroShouldNotBeFound("dataCriacao.specified=false");
    }

    @Test
    @Transactional
    public void getAllCarrosByDataCriacaoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataCriacao greater than or equals to DEFAULT_DATA_CRIACAO
        defaultCarroShouldBeFound("dataCriacao.greaterOrEqualThan=" + DEFAULT_DATA_CRIACAO);

        // Get all the carroList where dataCriacao greater than or equals to UPDATED_DATA_CRIACAO
        defaultCarroShouldNotBeFound("dataCriacao.greaterOrEqualThan=" + UPDATED_DATA_CRIACAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDataCriacaoIsLessThanSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataCriacao less than or equals to DEFAULT_DATA_CRIACAO
        defaultCarroShouldNotBeFound("dataCriacao.lessThan=" + DEFAULT_DATA_CRIACAO);

        // Get all the carroList where dataCriacao less than or equals to UPDATED_DATA_CRIACAO
        defaultCarroShouldBeFound("dataCriacao.lessThan=" + UPDATED_DATA_CRIACAO);
    }


    @Test
    @Transactional
    public void getAllCarrosByDataAtualizacaoIsEqualToSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataAtualizacao equals to DEFAULT_DATA_ATUALIZACAO
        defaultCarroShouldBeFound("dataAtualizacao.equals=" + DEFAULT_DATA_ATUALIZACAO);

        // Get all the carroList where dataAtualizacao equals to UPDATED_DATA_ATUALIZACAO
        defaultCarroShouldNotBeFound("dataAtualizacao.equals=" + UPDATED_DATA_ATUALIZACAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDataAtualizacaoIsInShouldWork() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataAtualizacao in DEFAULT_DATA_ATUALIZACAO or UPDATED_DATA_ATUALIZACAO
        defaultCarroShouldBeFound("dataAtualizacao.in=" + DEFAULT_DATA_ATUALIZACAO + "," + UPDATED_DATA_ATUALIZACAO);

        // Get all the carroList where dataAtualizacao equals to UPDATED_DATA_ATUALIZACAO
        defaultCarroShouldNotBeFound("dataAtualizacao.in=" + UPDATED_DATA_ATUALIZACAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDataAtualizacaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataAtualizacao is not null
        defaultCarroShouldBeFound("dataAtualizacao.specified=true");

        // Get all the carroList where dataAtualizacao is null
        defaultCarroShouldNotBeFound("dataAtualizacao.specified=false");
    }

    @Test
    @Transactional
    public void getAllCarrosByDataAtualizacaoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataAtualizacao greater than or equals to DEFAULT_DATA_ATUALIZACAO
        defaultCarroShouldBeFound("dataAtualizacao.greaterOrEqualThan=" + DEFAULT_DATA_ATUALIZACAO);

        // Get all the carroList where dataAtualizacao greater than or equals to UPDATED_DATA_ATUALIZACAO
        defaultCarroShouldNotBeFound("dataAtualizacao.greaterOrEqualThan=" + UPDATED_DATA_ATUALIZACAO);
    }

    @Test
    @Transactional
    public void getAllCarrosByDataAtualizacaoIsLessThanSomething() throws Exception {
        // Initialize the database
        carroRepository.saveAndFlush(carro);

        // Get all the carroList where dataAtualizacao less than or equals to DEFAULT_DATA_ATUALIZACAO
        defaultCarroShouldNotBeFound("dataAtualizacao.lessThan=" + DEFAULT_DATA_ATUALIZACAO);

        // Get all the carroList where dataAtualizacao less than or equals to UPDATED_DATA_ATUALIZACAO
        defaultCarroShouldBeFound("dataAtualizacao.lessThan=" + UPDATED_DATA_ATUALIZACAO);
    }


    @Test
    @Transactional
    public void getAllCarrosByTipoCarroIsEqualToSomething() throws Exception {
        // Initialize the database
        TipoCarro tipoCarro = TipoCarroResourceIntTest.createEntity(em);
        em.persist(tipoCarro);
        em.flush();
        carro.setTipoCarro(tipoCarro);
        carroRepository.saveAndFlush(carro);
        Long tipoCarroId = tipoCarro.getId();

        // Get all the carroList where tipoCarro equals to tipoCarroId
        defaultCarroShouldBeFound("tipoCarroId.equals=" + tipoCarroId);

        // Get all the carroList where tipoCarro equals to tipoCarroId + 1
        defaultCarroShouldNotBeFound("tipoCarroId.equals=" + (tipoCarroId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCarroShouldBeFound(String filter) throws Exception {
        restCarroMockMvc.perform(get("/api/carros?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carro.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())))
            .andExpect(jsonPath("$.[*].dataCriacao").value(hasItem(DEFAULT_DATA_CRIACAO.toString())))
            .andExpect(jsonPath("$.[*].dataAtualizacao").value(hasItem(DEFAULT_DATA_ATUALIZACAO.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCarroShouldNotBeFound(String filter) throws Exception {
        restCarroMockMvc.perform(get("/api/carros?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingCarro() throws Exception {
        // Get the carro
        restCarroMockMvc.perform(get("/api/carros/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCarro() throws Exception {
        // Initialize the database
        carroService.save(carro);

        int databaseSizeBeforeUpdate = carroRepository.findAll().size();

        // Update the carro
        Carro updatedCarro = carroRepository.findOne(carro.getId());
        updatedCarro
            .codigo(UPDATED_CODIGO)
            .descricao(UPDATED_DESCRICAO)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataAtualizacao(UPDATED_DATA_ATUALIZACAO);

        restCarroMockMvc.perform(put("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCarro)))
            .andExpect(status().isOk());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeUpdate);
        Carro testCarro = carroList.get(carroList.size() - 1);
        assertThat(testCarro.getCodigo()).isEqualTo(UPDATED_CODIGO);
        assertThat(testCarro.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCarro.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testCarro.getDataAtualizacao()).isEqualTo(UPDATED_DATA_ATUALIZACAO);

        // Validate the Carro in Elasticsearch
        Carro carroEs = carroSearchRepository.findOne(testCarro.getId());
        assertThat(carroEs).isEqualToComparingFieldByField(testCarro);
    }

    @Test
    @Transactional
    public void updateNonExistingCarro() throws Exception {
        int databaseSizeBeforeUpdate = carroRepository.findAll().size();

        // Create the Carro

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCarroMockMvc.perform(put("/api/carros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(carro)))
            .andExpect(status().isCreated());

        // Validate the Carro in the database
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCarro() throws Exception {
        // Initialize the database
        carroService.save(carro);

        int databaseSizeBeforeDelete = carroRepository.findAll().size();

        // Get the carro
        restCarroMockMvc.perform(delete("/api/carros/{id}", carro.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean carroExistsInEs = carroSearchRepository.exists(carro.getId());
        assertThat(carroExistsInEs).isFalse();

        // Validate the database is empty
        List<Carro> carroList = carroRepository.findAll();
        assertThat(carroList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCarro() throws Exception {
        // Initialize the database
        carroService.save(carro);

        // Search the carro
        restCarroMockMvc.perform(get("/api/_search/carros?query=id:" + carro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carro.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())))
            .andExpect(jsonPath("$.[*].dataCriacao").value(hasItem(DEFAULT_DATA_CRIACAO.toString())))
            .andExpect(jsonPath("$.[*].dataAtualizacao").value(hasItem(DEFAULT_DATA_ATUALIZACAO.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Carro.class);
        Carro carro1 = new Carro();
        carro1.setId(1L);
        Carro carro2 = new Carro();
        carro2.setId(carro1.getId());
        assertThat(carro1).isEqualTo(carro2);
        carro2.setId(2L);
        assertThat(carro1).isNotEqualTo(carro2);
        carro1.setId(null);
        assertThat(carro1).isNotEqualTo(carro2);
    }
}
