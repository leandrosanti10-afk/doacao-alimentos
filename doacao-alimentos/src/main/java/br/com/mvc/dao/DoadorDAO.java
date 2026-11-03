package br.com.mvc.dao;

import br.com.mvc.api.ApiClient;
import br.com.mvc.api.ApiException;
import br.com.mvc.model.Doador;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO de Doadores do projeto Web: usa somente HTTP + JSON + JWT.
 * O acesso JDBC/SQL e as regras definitivas de negocio ficam na API REST.
 * As assinaturas publicas foram preservadas para nao alterar o DoadorService.
 */
public class DoadorDAO {

    private final ApiClient api;

    public DoadorDAO() {
        this(new ApiClient());
    }

    /** Construtor para testes; o cliente gerencia o JWT automaticamente. */
    public DoadorDAO(ApiClient api) {
        this.api = java.util.Objects.requireNonNull(api, "ApiClient obrigatorio.");
    }

    public Long inserir(Doador doador) {
        try {
            Doador criado = api.post("doadores", doador, Doador.class);
            if (criado == null || criado.getId() == null || criado.getId() <= 0) {
                throw new IllegalStateException("A API nao retornou o ID do doador cadastrado.");
            }
            return criado.getId();
        } catch (ApiException ex) {
            throw tratarErroValidacao(ex);
        }
    }

    public List<Doador> listar() {
        Doador[] recebidos = api.get("doadores", Doador[].class);
        if (recebidos == null) {
            throw new IllegalStateException("A API nao retornou a lista de doadores.");
        }
        return new ArrayList<>(Arrays.asList(recebidos));
    }

    public Doador buscarPorId(Long id) {
        validarId(id);
        try {
            return api.get("doadores/" + id, Doador.class);
        } catch (ApiException ex) {
            // O Service/Controller Web original espera null quando o registro nao existe.
            if (ex.getStatusHttp() == 404) {
                return null;
            }
            throw ex;
        }
    }

    public void alterar(Doador doador) {
        if (doador == null) {
            throw new IllegalArgumentException("Doador obrigatorio.");
        }
        validarId(doador.getId());
        try {
            api.put("doadores/" + doador.getId(), doador, Doador.class);
        } catch (ApiException ex) {
            throw tratarErroValidacao(ex);
        }
    }

    public void deletar(Long id) {
        validarId(id);
        try {
            api.delete("doadores/" + id);
        } catch (ApiException ex) {
            // DoadorController trata IllegalStateException para mostrar erro na lista.
            if (ex.getStatusHttp() == 409) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
            throw ex;
        }
    }

    public boolean possuiDoacoes(Long id) {
        validarId(id);
        // Endpoint de consulta agregado ainda nao existe; filtra JSON da API de Doacoes.
        // A API tambem valida essa regra no DELETE, inclusive em caso de concorrencia.
        JsonArray doacoes = api.get("doacoes", JsonArray.class);
        if (doacoes == null) {
            throw new IllegalStateException("A API nao retornou a lista de doacoes.");
        }
        for (JsonElement elemento : doacoes) {
            if (elemento == null || !elemento.isJsonObject()) {
                continue;
            }
            JsonObject doacao = elemento.getAsJsonObject();
            if (!doacao.has("doador") || !doacao.get("doador").isJsonObject()) {
                continue;
            }
            JsonObject doador = doacao.getAsJsonObject("doador");
            if (doador.has("id") && !doador.get("id").isJsonNull()) {
                try {
                    if (id.equals(doador.get("id").getAsLong())) {
                        return true;
                    }
                } catch (NumberFormatException | UnsupportedOperationException ignorado) {
                    // Entrada inesperada nao deve produzir um falso positivo.
                }
            }
        }
        return false;
    }

    public boolean existeCpfCnpj(String cpfCnpj, Long ignorarId) {
        return existeCampo(cpfCnpj, ignorarId, Doador::getCpfCnpj);
    }

    public boolean existeEmail(String email, Long ignorarId) {
        return existeCampo(email, ignorarId, Doador::getEmail);
    }

    public boolean existeTelefone(String telefone, Long ignorarId) {
        return existeCampo(telefone, ignorarId, Doador::getTelefone);
    }

    private boolean existeCampo(String valor, Long ignorarId,
            java.util.function.Function<Doador, String> obterCampo) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        // Mantem as verificacoes existentes do Service sem SQL no Web.
        // A API continua sendo a autoridade final sobre duplicidade e validacoes.
        for (Doador existente : listar()) {
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
            throw new IllegalArgumentException("ID do doador deve ser positivo.");
        }
    }

    private static RuntimeException tratarErroValidacao(ApiException erro) {
        if (erro.getStatusHttp() == 400) {
            return new IllegalArgumentException(erro.getMessage(), erro);
        }
        return erro;
    }
}
