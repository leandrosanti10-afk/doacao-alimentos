package br.com.mvc.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Cliente REST centralizado da aplicacao Web (8080) para a API (8081).
 * O JWT e obtido automaticamente; nunca deve ser enviado ao navegador.
 */
public final class ApiClient {
    private static final String URL_PADRAO = "http://localhost:8081/doafacil-api/api";
    private final HttpClient http;
    private final Gson gson;
    private final URI base;
    private final ApiAuthService autenticacao;

    public ApiClient() {
        this(System.getenv("DOAFACIL_API_URL"),
                System.getenv("DOAFACIL_API_USER"),
                System.getenv("DOAFACIL_API_PASSWORD"));
    }

    public ApiClient(String baseUrl, String usuario, String senha) {
        this.base = prepararBase(baseUrl);
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.gson = criarGson();
        this.autenticacao = new ApiAuthService(this, usuario, senha);
    }

    /** Consulta protegida: GET /api/{rota}. Ex.: get("doadores", Doador[].class). */
    public <T> T get(String rota, Class<T> tipo) {
        return executar("GET", rota, null, tipo, true);
    }

    /** Cadastro protegido; retorna o objeto recebido da API (HTTP 201). */
    public <T> T post(String rota, Object corpo, Class<T> tipo) {
        return executar("POST", rota, corpo, tipo, true);
    }

    /** Atualizacao protegida; o ID faz parte da rota. */
    public <T> T put(String rota, Object corpo, Class<T> tipo) {
        return executar("PUT", rota, corpo, tipo, true);
    }

    /** Exclusao protegida; HTTP 204 nao possui corpo. */
    public void delete(String rota) {
        executar("DELETE", rota, null, Void.class, true);
    }

    /** Usado no diagnostico publico /api/status. */
    public <T> T getPublico(String rota, Class<T> tipo) {
        return executar("GET", rota, null, tipo, false);
    }

    /** Utilizado apenas para login; nao anexa um token ainda inexistente. */
    <T> T postPublico(String rota, Object corpo, Class<T> tipo) {
        return executar("POST", rota, corpo, tipo, false);
    }

    private <T> T executar(String metodo, String rota, Object corpo,
                          Class<T> tipo, boolean protegido) {
        URI destino = resolverRota(rota);
        String json = corpo == null ? null : gson.toJson(corpo);
        // Uma resposta 401 do filtro JWT pode ser renovada com seguranca:
        // o filtro barra a chamada antes de atingir o Controller REST.
        int tentativas = protegido ? 2 : 1;

        for (int tentativa = 0; tentativa < tentativas; tentativa++) {
            HttpRequest.Builder requisicao = HttpRequest.newBuilder(destino)
                    .timeout(Duration.ofSeconds(15))
                    .header("Accept", "application/json");
            if (protegido) {
                requisicao.header("Authorization", "Bearer " + autenticacao.obterToken());
            }
            switch (metodo) {
                case "GET" -> requisicao.GET();
                case "DELETE" -> requisicao.DELETE();
                case "POST", "PUT" -> {
                    requisicao.header("Content-Type", "application/json; charset=UTF-8");
                    HttpRequest.BodyPublisher publicador = HttpRequest.BodyPublishers
                            .ofString(json == null ? "" : json, StandardCharsets.UTF_8);
                    requisicao.method(metodo, publicador);
                }
                default -> throw new IllegalArgumentException("Metodo HTTP nao suportado.");
            }

            final HttpResponse<String> resposta;
            try {
                resposta = http.send(requisicao.build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new ApiException(0, "Comunicacao com a API interrompida.", ex);
            } catch (IOException ex) {
                // Nao reenvia POST/PUT/DELETE por erro de rede: pode ter gravado no banco.
                throw new ApiException(0, "Nao foi possivel conectar a API DoaFacil.", ex);
            }

            int status = resposta.statusCode();
            if (status == 401 && protegido && tentativa == 0) {
                autenticacao.invalidarToken();
                continue;
            }
            if (status < 200 || status >= 300) {
                throw new ApiException(status, mensagemErro(resposta.body(), status));
            }
            if (tipo == Void.class) {
                return null;
            }
            if (resposta.body() == null || resposta.body().isBlank()) {
                throw new ApiException(0, "A API respondeu sem o JSON esperado.");
            }
            try {
                return gson.fromJson(resposta.body(), tipo);
            } catch (JsonParseException ex) {
                throw new ApiException(0, "A API retornou JSON invalido.", ex);
            }
        }
        throw new ApiException(401, "A API recusou a autenticacao.");
    }

    private String mensagemErro(String corpo, int status) {
        if (corpo != null && !corpo.isBlank()) {
            try {
                JsonObject objeto = JsonParser.parseString(corpo).getAsJsonObject();
                if (objeto.has("erro") && !objeto.get("erro").isJsonNull()) {
                    String erro = objeto.get("erro").getAsString();
                    if (!erro.isBlank()) {
                        return erro.length() > 250 ? erro.substring(0, 250) : erro;
                    }
                }
            } catch (RuntimeException ignorado) {
                // Resposta de erro nao JSON; nao expor HTML ou stack traces externos.
            }
        }
        return "A API retornou HTTP " + status + ".";
    }

    private static URI prepararBase(String configurada) {
        String url = configurada == null || configurada.isBlank()
                ? URL_PADRAO : configurada.trim();
        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("DOAFACIL_API_URL invalida.", ex);
        }
        if (!("http".equalsIgnoreCase(uri.getScheme())
                || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null || uri.getRawQuery() != null
                || uri.getRawFragment() != null || uri.getRawUserInfo() != null) {
            throw new IllegalArgumentException("DOAFACIL_API_URL deve ser uma URL HTTP(S) valida.");
        }
        return URI.create(uri.toString().replaceAll("/+$", "") + "/");
    }

    private URI resolverRota(String rota) {
        if (rota == null || rota.isBlank()) {
            throw new IllegalArgumentException("A rota da API e obrigatoria.");
        }
        String relativa = rota.startsWith("/") ? rota.substring(1) : rota;
        if (!relativa.matches("[A-Za-z0-9_/-]+") || relativa.contains("..")
                || relativa.startsWith("/")) {
            throw new IllegalArgumentException("Rota da API invalida.");
        }
        URI destino = base.resolve(relativa);
        if (!destino.getPath().startsWith(base.getPath())) {
            throw new IllegalArgumentException("Rota fora da API configurada.");
        }
        return destino;
    }

    private static Gson criarGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                    @Override public void write(JsonWriter saida, LocalDate valor) throws IOException {
                        if (valor == null) saida.nullValue();
                        else saida.value(valor.toString());
                    }
                    @Override public LocalDate read(JsonReader entrada) throws IOException {
                        if (entrada.peek() == JsonToken.NULL) {
                            entrada.nextNull();
                            return null;
                        }
                        try {
                            return LocalDate.parse(entrada.nextString());
                        } catch (DateTimeParseException ex) {
                            throw new JsonParseException("Data invalida recebida da API.", ex);
                        }
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                    @Override public void write(JsonWriter saida, LocalDateTime valor)
                            throws IOException {
                        if (valor == null) saida.nullValue();
                        else saida.value(valor.toString());
                    }
                    @Override public LocalDateTime read(JsonReader entrada) throws IOException {
                        if (entrada.peek() == JsonToken.NULL) {
                            entrada.nextNull();
                            return null;
                        }
                        try {
                            return LocalDateTime.parse(entrada.nextString());
                        } catch (DateTimeParseException ex) {
                            throw new JsonParseException("Data/hora invalida recebida da API.", ex);
                        }
                    }
                }).create();
    }
}
