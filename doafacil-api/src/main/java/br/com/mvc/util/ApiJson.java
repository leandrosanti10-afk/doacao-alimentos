package br.com.mvc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Utilitario de leitura e escrita de JSON para os Controllers da API.
 * Mantem as respostas REST independentes das paginas JSP existentes.
 */
public final class ApiJson {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate valor) throws IOException {
                    if (valor == null) {
                        out.nullValue();
                    } else {
                        out.value(valor.toString()); // yyyy-MM-dd
                    }
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    try {
                        return LocalDate.parse(in.nextString());
                    } catch (DateTimeParseException ex) {
                        throw new JsonParseException("Data invalida; esperado yyyy-MM-dd.", ex);
                    }
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                @Override
                public void write(JsonWriter out, LocalDateTime valor) throws IOException {
                    if (valor == null) {
                        out.nullValue();
                    } else {
                        out.value(valor.toString()); // ISO 8601
                    }
                }

                @Override
                public LocalDateTime read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    try {
                        return LocalDateTime.parse(in.nextString());
                    } catch (DateTimeParseException ex) {
                        throw new JsonParseException("Data/hora invalida; use ISO 8601.", ex);
                    }
                }
            })
            .create();

    private ApiJson() {
        // Classe utilitaria: sem instancias.
    }

    /** Escreve o status HTTP e, quando presente, o corpo JSON. */
    public static void responder(HttpServletResponse resp, int status, Object dados)
            throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setStatus(status);
        if (dados != null) {
            GSON.toJson(dados, resp.getWriter());
        }
    }

    /** Padroniza o formato dos erros: { "erro": "mensagem" }. */
    public static void erro(HttpServletResponse resp, int status, String mensagem)
            throws IOException {
        responder(resp, status, Map.of("erro", mensagem));
    }

    /** Le o JSON do corpo; o Controller decide o status caso seja invalido. */
    public static <T> T lerCorpo(HttpServletRequest req, Class<T> tipo)
            throws IOException {
        T objeto = GSON.fromJson(req.getReader(), tipo);
        if (objeto == null) {
            throw new JsonParseException("O corpo da requisicao JSON esta vazio.");
        }
        return objeto;
    }
}
