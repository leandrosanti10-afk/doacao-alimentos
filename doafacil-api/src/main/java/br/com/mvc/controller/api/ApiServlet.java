package br.com.mvc.controller.api;

import br.com.mvc.util.ApiJson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ApiServlet extends HttpServlet {
    protected void responderJson(HttpServletResponse resp, int status, Object dados) throws IOException {
        ApiJson.responder(resp, status, dados);
    }

    protected void responderErro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        ApiJson.erro(resp, status, mensagem);
    }

    protected <T> T lerJson(HttpServletRequest req, Class<T> tipo) throws IOException {
        return ApiJson.lerCorpo(req, tipo);
    }
}
