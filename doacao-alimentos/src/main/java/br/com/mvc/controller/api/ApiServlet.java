package br.com.mvc.controller.api;

import br.com.mvc.util.ApiJson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Base para os futuros Controllers REST.
 * O HTTP devolve JSON; regras de negocio permanecem no Service.
 */
public abstract class ApiServlet extends HttpServlet {

    protected final void responderJson(HttpServletResponse resp, int status, Object dados)
            throws IOException {
        ApiJson.responder(resp, status, dados);
    }

    protected final void responderErro(HttpServletResponse resp, int status, String mensagem)
            throws IOException {
        ApiJson.erro(resp, status, mensagem);
    }

    protected final <T> T lerJson(HttpServletRequest req, Class<T> tipo)
            throws IOException {
        return ApiJson.lerCorpo(req, tipo);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Metodo GET nao permitido nesta rota.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Metodo POST nao permitido nesta rota.");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Metodo PUT nao permitido nesta rota.");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Metodo DELETE nao permitido nesta rota.");
    }
}
