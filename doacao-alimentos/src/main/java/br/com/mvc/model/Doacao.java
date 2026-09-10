package br.com.mvc.model;

import java.time.LocalDate;

public class Doacao {

    private Long id;

    private Doador doador;

    private Instituicao instituicao;

    private String descricao;

    private LocalDate dataDoacao;

    public Doacao() {
    }

    public Doacao(
            Long id,
            Doador doador,
            Instituicao instituicao,
            String descricao,
            LocalDate dataDoacao) {

        this.id = id;
        this.doador = doador;
        this.instituicao = instituicao;
        this.descricao = descricao;
        this.dataDoacao = dataDoacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doador getDoador() {
        return doador;
    }

    public void setDoador(Doador doador) {
        this.doador = doador;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(LocalDate dataDoacao) {
        this.dataDoacao = dataDoacao;
    }
}