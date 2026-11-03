package br.com.mvc.dao;

import br.com.mvc.api.ApiClient;
import br.com.mvc.api.ApiException;
import br.com.mvc.model.Doacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * DAO do projeto Web: opera exclusivamente por REST, JSON e JWT.
 * O JOIN e a persistencia das doacoes permanecem no DAO da API (8081).
 * Mantem as assinaturas publicas esperadas pelo DoacaoService.
 */
public class DoacaoDAO {

    private final ApiClient api;

    public DoacaoDAO() {
        this(new ApiClient());
    }

    public DoacaoDAO(ApiClient api) {
        this.api = Objects.requireNonNull(api, "ApiClient obrigatorio.");
    }

    public void inserir(Doacao doacao) {
        if (doacao == null) {
            throw new IllegalArgumentException("Doacao obrigatoria.");
        }
        try {
            Doacao criada = api.post("doacoes", doacao, Doacao.class);
            if (criada == null || criada.getId() == null || criada.getId() <= 0) {
                throw new IllegalStateException("A API nao retornou o ID da doacao cadastrada.");
            }
            doacao.setId(criada.getId());
        } catch (ApiException ex) {
            throw tratarErroValidacao(ex);
        }
    }

    public List<Doacao> listar() {
        Doacao[] recebidas = api.get("doacoes", Doacao[].class);
        if (recebidas == null) {
            throw new IllegalStateException("A API nao retornou a lista de doacoes.");
        }
        return new ArrayList<>(Arrays.asList(recebidas));
    }

    public Doacao buscarPorId(Long id) {
        validarId(id);
        try {
            return api.get("doacoes/" + id, Doacao.class);
        } catch (ApiException ex) {
            if (ex.getStatusHttp() == 404) {
                return null;
            }
            throw ex;
        }
    }

    public void alterar(Doacao doacao) {
        if (doacao == null) {
            throw new IllegalArgumentException("Doacao obrigatoria.");
        }
        validarId(doacao.getId());
        try {
            api.put("doacoes/" + doacao.getId(), doacao, Doacao.class);
        } catch (ApiException ex) {
            throw tratarErroValidacao(ex);
        }
    }

    public void deletar(Long id) {
        validarId(id);
        api.delete("doacoes/" + id);
    }

    private static void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da doacao deve ser positivo.");
        }
    }

    private static RuntimeException tratarErroValidacao(ApiException erro) {
        if (erro.getStatusHttp() == 400 || erro.getStatusHttp() == 404) {
            // DoacaoController original captura IllegalArgumentException nos formularios.
            return new IllegalArgumentException(erro.getMessage(), erro);
        }
        return erro;
    }
}
