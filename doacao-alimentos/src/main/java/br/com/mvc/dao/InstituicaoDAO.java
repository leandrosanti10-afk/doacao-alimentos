package br.com.mvc.dao;

import br.com.mvc.api.ApiClient;
import br.com.mvc.api.ApiException;
import br.com.mvc.model.Doacao;
import br.com.mvc.model.Instituicao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * DAO do projeto Web: todas as operacoes usam HTTP + JSON + JWT.
 * A API REST (8081) e a unica responsavel pelo acesso ao MySQL.
 * Mantem os metodos publicos utilizados pelo InstituicaoService.
 */
public class InstituicaoDAO {

    private final ApiClient api;

    public InstituicaoDAO() {
        this(new ApiClient());
    }

    /** Permite injetar um cliente HTTP em testes. */
    public InstituicaoDAO(ApiClient api) {
        this.api = Objects.requireNonNull(api, "ApiClient obrigatorio.");
    }

    public void inserir(Instituicao instituicao) {
        if (instituicao == null) {
            throw new IllegalArgumentException("Instituicao obrigatoria.");
        }
        try {
            Instituicao criada = api.post("instituicoes", instituicao, Instituicao.class);
            if (criada == null || criada.getId() == null || criada.getId() <= 0) {
                throw new IllegalStateException("A API nao retornou o ID da instituicao cadastrada.");
            }
            // A assinatura original permanece void; o ID fica disponivel no objeto.
            instituicao.setId(criada.getId());
            instituicao.setDataCadastro(criada.getDataCadastro());
        } catch (ApiException ex) {
            throw tratarErroValidacao(ex);
        }
    }

    public List<Instituicao> listar() {
        Instituicao[] recebidas = api.get("instituicoes", Instituicao[].class);
        if (recebidas == null) {
            throw new IllegalStateException("A API nao retornou a lista de instituicoes.");
        }
        return new ArrayList<>(Arrays.asList(recebidas));
    }

    public Instituicao buscarPorId(Long id) {
        validarId(id);
        try {
            return api.get("instituicoes/" + id, Instituicao.class);
        } catch (ApiException ex) {
            if (ex.getStatusHttp() == 404) {
                return null;
            }
            throw ex;
        }
    }

    public void alterar(Instituicao instituicao) {
        if (instituicao == null) {
            throw new IllegalArgumentException("Instituicao obrigatoria.");
        }
        validarId(instituicao.getId());
        try {
            api.put("instituicoes/" + instituicao.getId(), instituicao, Instituicao.class);
        } catch (ApiException ex) {
            throw tratarErroValidacao(ex);
        }
    }

    public void deletar(Long id) {
        validarId(id);
        try {
            api.delete("instituicoes/" + id);
        } catch (ApiException ex) {
            if (ex.getStatusHttp() == 409) {
                // InstituicaoController ja trata IllegalStateException para exibicao na lista.
                throw new IllegalStateException(ex.getMessage(), ex);
            }
            throw ex;
        }
    }

    public boolean possuiDoacoes(Long id) {
        validarId(id);
        // O contrato atual nao oferece /instituicoes/{id}/doacoes.
        // Usamos a listagem JSON existente, sem SQL no Web.
        Doacao[] doacoes = api.get("doacoes", Doacao[].class);
        if (doacoes == null) {
            throw new IllegalStateException("A API nao retornou a lista de doacoes.");
        }
        for (Doacao doacao : doacoes) {
            if (doacao == null || doacao.getInstituicao() == null) {
                continue;
            }
            if (id.equals(doacao.getInstituicao().getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean existeEmail(String email, Long ignorarId) {
        return existeCampo(email, ignorarId, Instituicao::getEmail);
    }

    public boolean existeTelefone(String telefone, Long ignorarId) {
        return existeCampo(telefone, ignorarId, Instituicao::getTelefone);
    }

    private boolean existeCampo(String valor, Long ignorarId,
            Function<Instituicao, String> obterCampo) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        for (Instituicao existente : listar()) {
            if (existente == null || existente.getId() == null
                    || existente.getId().equals(ignorarId)) {
                continue;
            }
            String campo = obterCampo.apply(existente);
            if (campo != null && campo.trim().equalsIgnoreCase(valor.trim())) {
                return true;
            }
        }
        return false;
    }

    private static void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da instituicao deve ser positivo.");
        }
    }

    private static RuntimeException tratarErroValidacao(ApiException erro) {
        if (erro.getStatusHttp() == 400) {
            return new IllegalArgumentException(erro.getMessage(), erro);
        }
        return erro;
    }
}
