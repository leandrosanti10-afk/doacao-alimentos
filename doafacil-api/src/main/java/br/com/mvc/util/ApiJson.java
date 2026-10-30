package br.com.mvc.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public final class ApiJson {
    private static final Gson GSON = new Gson();

    private ApiJson() {
    }

    public static void responder(HttpServletResponse resp, int status, Object dados) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        if (dados != null) {
            GSON.toJson(dados, resp.getWriter());
        }
    }

    public static void erro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        responder(resp, status, Map.of("erro", mensagem));
    }

    public static <T> T lerCorpo(HttpServletRequest req, Class<T> tipo) throws IOException {
        T objeto = GSON.fromJson(req.getReader(), tipo);
        if (objeto == null) {
            throw new JsonParseException("O corpo JSON esta vazio.");
        }
        return objeto;
    }
}
