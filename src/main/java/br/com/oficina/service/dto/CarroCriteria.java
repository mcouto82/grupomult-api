package br.com.oficina.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;


import io.github.jhipster.service.filter.LocalDateFilter;



/**
 * Criteria class for the Carro entity. This class is used in CarroResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /carros?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CarroCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private LongFilter codigo;

    private StringFilter descricao;

    private LocalDateFilter dataCriacao;

    private LocalDateFilter dataAtualizacao;

    private LongFilter tipoCarroId;

    public CarroCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getCodigo() {
        return codigo;
    }

    public void setCodigo(LongFilter codigo) {
        this.codigo = codigo;
    }

    public StringFilter getDescricao() {
        return descricao;
    }

    public void setDescricao(StringFilter descricao) {
        this.descricao = descricao;
    }

    public LocalDateFilter getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateFilter dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateFilter getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateFilter dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LongFilter getTipoCarroId() {
        return tipoCarroId;
    }

    public void setTipoCarroId(LongFilter tipoCarroId) {
        this.tipoCarroId = tipoCarroId;
    }

    @Override
    public String toString() {
        return "CarroCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (codigo != null ? "codigo=" + codigo + ", " : "") +
                (descricao != null ? "descricao=" + descricao + ", " : "") +
                (dataCriacao != null ? "dataCriacao=" + dataCriacao + ", " : "") +
                (dataAtualizacao != null ? "dataAtualizacao=" + dataAtualizacao + ", " : "") +
                (tipoCarroId != null ? "tipoCarroId=" + tipoCarroId + ", " : "") +
            "}";
    }

}
