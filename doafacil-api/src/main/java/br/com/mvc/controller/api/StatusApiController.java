package br.com.mvc.controller.api;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/status")
public class StatusApiController extends ApiServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        responderJson(resp, HttpServletResponse.SC_OK, Map.of(
                "aplicacao", "DoaFacil API",
                "status", "ok",
                "mensagem", "API independente funcionando"
        ));
    }
}
