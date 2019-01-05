package br.com.oficina.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Carro.
 */
@Entity
@Table(name = "carro")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "carro")
public class Carro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "codigo")
    private Long codigo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    @ManyToOne(optional = false)
    @NotNull
    private TipoCarro tipoCarro;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public Carro codigo(Long codigo) {
        this.codigo = codigo;
        return this;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Carro descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public Carro dataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
        return this;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataAtualizacao() {
        return dataAtualizacao;
    }

    public Carro dataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
        return this;
    }

    public void setDataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public TipoCarro getTipoCarro() {
        return tipoCarro;
    }

    public Carro tipoCarro(TipoCarro tipoCarro) {
        this.tipoCarro = tipoCarro;
        return this;
    }

    public void setTipoCarro(TipoCarro tipoCarro) {
        this.tipoCarro = tipoCarro;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Carro carro = (Carro) o;
        if (carro.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), carro.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Carro{" +
            "id=" + getId() +
            ", codigo='" + getCodigo() + "'" +
            ", descricao='" + getDescricao() + "'" +
            ", dataCriacao='" + getDataCriacao() + "'" +
            ", dataAtualizacao='" + getDataAtualizacao() + "'" +
            "}";
    }
}
