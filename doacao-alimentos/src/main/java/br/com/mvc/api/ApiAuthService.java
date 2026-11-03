package br.com.mvc.api;

import java.time.Instant;
import java.util.Map;

/**
 * Autenticacao servidor-a-servidor na API: guarda JWT somente em memoria.
 * Compartilhado entre requisicoes concorrentes; renovacao sincronizada.
 */
public final class ApiAuthService {
    private final ApiClient cliente;
    private final String usuario;
    private final String senha;
    private String token;
    private Instant renovarEm = Instant.EPOCH;

    ApiAuthService(ApiClient cliente, String usuario, String senha) {
        this.cliente = cliente;
        this.usuario = usuario;
        this.senha = senha;
    }

    public synchronized String obterToken() {
        if (token != null && Instant.now().isBefore(renovarEm)) {
            return token;
        }
        if (usuario == null || usuario.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalStateException(
                    "Configure DOAFACIL_API_USER e DOAFACIL_API_PASSWORD no servidor Web.");
        }

        RespostaLogin resposta = cliente.postPublico(
                "auth/login", Map.of("usuario", usuario, "senha", senha), RespostaLogin.class);

        if (resposta == null || resposta.access_token == null
                || resposta.access_token.isBlank() || !"Bearer".equals(resposta.token_type)
                || resposta.expires_in <= 0) {
            throw new ApiException(0, "A API retornou uma resposta de login invalida.");
        }

        token = resposta.access_token;
        // Margem para evitar que o token expire durante uma requisicao.
        renovarEm = Instant.now().plusSeconds(Math.max(1L, resposta.expires_in - 30L));
        return token;
    }

    public synchronized void invalidarToken() {
        token = null;
        renovarEm = Instant.EPOCH;
    }

    private static final class RespostaLogin {
        String access_token;
        String token_type;
        long expires_in;
    }
}
