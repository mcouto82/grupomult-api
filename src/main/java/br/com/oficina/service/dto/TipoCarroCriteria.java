package br.com.oficina.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the TipoCarro entity. This class is used in TipoCarroResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /tipo-carros?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TipoCarroCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private LongFilter codigo;

    private StringFilter descricao;

    public TipoCarroCriteria() {
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

    @Override
    public String toString() {
        return "TipoCarroCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (codigo != null ? "codigo=" + codigo + ", " : "") +
                (descricao != null ? "descricao=" + descricao + ", " : "") +
            "}";
    }

}