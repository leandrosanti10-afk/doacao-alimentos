package br.com.mvc.controller.api;

import br.com.mvc.security.JwtService;
import br.com.mvc.util.ApiJson;
import com.google.gson.JsonParseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

/** Login de uma conta de integracao Web -> API configurada por ambiente. */
@WebServlet("/api/auth/login")
public class AuthApiController extends ApiServlet {

    private static class Credenciais {
        String usuario;
        String senha;
    }

    private static boolean igualConstante(String recebido, String esperado) {
        if (recebido == null || esperado == null) {
            return false;
        }
        return MessageDigest.isEqual(
                recebido.getBytes(StandardCharsets.UTF_8),
                esperado.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Pragma", "no-cache");

        String usuarioConfigurado = System.getenv("API_AUTH_USER");
        String senhaConfigurada = System.getenv("API_AUTH_PASSWORD");
        if (usuarioConfigurado == null || usuarioConfigurado.isBlank()
                || senhaConfigurada == null || senhaConfigurada.isBlank()) {
            ApiJson.erro(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Autenticacao indisponivel: configuracao do servidor incompleta.");
            return;
        }

        final Credenciais credenciais;
        try {
            credenciais = ApiJson.lerCorpo(req, Credenciais.class);
        } catch (JsonParseException | IllegalStateException ex) {
            ApiJson.erro(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON de login invalido.");
            return;
        }
        if (credenciais.usuario == null || credenciais.senha == null
                || credenciais.usuario.isBlank() || credenciais.senha.isBlank()) {
            ApiJson.erro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Informe usuario e senha.");
            return;
        }
        if (!igualConstante(credenciais.usuario, usuarioConfigurado)
                || !igualConstante(credenciais.senha, senhaConfigurada)) {
            ApiJson.erro(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    "Credenciais invalidas.");
            return;
        }

        try {
            JwtService jwt = new JwtService();
            String token = jwt.emitir(usuarioConfigurado);
            responderJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "access_token", token,
                    "token_type", "Bearer",
                    "expires_in", jwt.getDuracaoSegundos()
            ));
        } catch (IllegalStateException ex) {
            getServletContext().log("JWT nao configurado corretamente.");
            ApiJson.erro(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Autenticacao indisponivel.");
        }
    }
}
