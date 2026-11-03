package br.com.mvc.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Diagnostico de leitura: Web -> API com JWT, sem modificar dados.
 * Executado pelo script scripts/testarClienteHttp.ps1, nao e um Servlet.
 */
public final class ApiConexaoSmokeTest {
    private ApiConexaoSmokeTest() { }

    public static void main(String[] args) {
        try {
            ApiClient api = new ApiClient();
            JsonObject status = api.getPublico("status", JsonObject.class);
            if (status == null || !status.has("status")
                    || !"ok".equals(status.get("status").getAsString())) {
                throw new IllegalStateException("Status publico nao confirmou a API.");
            }
            System.out.println("APROVADO: GET /api/status (HTTP 200)");

            JsonArray doadores = api.get("doadores", JsonArray.class);
            System.out.println("APROVADO: GET /api/doadores com JWT (HTTP 200, registros: "
                    + doadores.size() + ")");

            JsonArray instituicoes = api.get("instituicoes", JsonArray.class);
            System.out.println("APROVADO: GET /api/instituicoes com JWT (HTTP 200, registros: "
                    + instituicoes.size() + ")");

            JsonArray doacoes = api.get("doacoes", JsonArray.class);
            System.out.println("APROVADO: GET /api/doacoes com JWT (HTTP 200, registros: "
                    + doacoes.size() + ")");

            System.out.println("TODOS OS TESTES DO CLIENTE HTTP PASSARAM.");
        } catch (RuntimeException ex) {
            System.err.println("FALHOU: " + ex.getMessage());
            System.exit(1);
        }
    }
}
