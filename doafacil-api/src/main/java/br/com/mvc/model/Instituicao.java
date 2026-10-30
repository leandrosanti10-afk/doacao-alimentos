package br.com.mvc.model;

import java.time.LocalDateTime;

public class Instituicao {

    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
    private String email;
    private String cidade;
    private LocalDateTime dataCadastro;

    public Instituicao() {
    }

    public Instituicao(
            Long id,
            String nome,
            String endereco,
            String telefone,
            String email,
            String cidade,
            LocalDateTime dataCadastro) {

        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.cidade = cidade;
        this.dataCadastro = dataCadastro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}