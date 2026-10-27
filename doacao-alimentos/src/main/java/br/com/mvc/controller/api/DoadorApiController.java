package br.com.mvc.controller.api;

import br.com.mvc.model.Doador;
import br.com.mvc.service.DoadorService;
import com.google.gson.JsonParseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Endpoints REST de doadores: GET (lista/detalhe) e POST (cadastro).
 * Mantem as regras de negocio no Service e a persistencia no DAO.
 */
@WebServlet("/api/doadores/*")
public class DoadorApiController extends ApiServlet {

    private final DoadorService doadorService = new DoadorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        try {
            if (caminho == null || "/".equals(caminho)) {
                responderJson(resp, HttpServletResponse.SC_OK, doadorService.listar());
                return;
            }

            // Apenas /api/doadores/{id}; caminhos com outras barras nao existem.
            if (!caminho.matches("/[^/]+")) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Rota de doadores nao encontrada.");
                return;
            }

            Long id;
            try {
                id = Long.valueOf(caminho.substring(1));
            } catch (NumberFormatException e) {
                responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "O ID do doador deve ser um numero inteiro positivo.");
                return;
            }
            if (id <= 0) {
                responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "O ID do doador deve ser um numero inteiro positivo.");
                return;
            }

            Doador doador = doadorService.buscarPorId(id);
            if (doador == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doador nao encontrado.");
                return;
            }
            responderJson(resp, HttpServletResponse.SC_OK, doador);
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao consultar doadores na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao consultar doadores.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        if (caminho != null && !"/".equals(caminho)) {
            responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "POST e permitido apenas em /api/doadores.");
            return;
        }
        try {
            req.setCharacterEncoding("UTF-8");
            Doador doador = lerJson(req, Doador.class);
            // ID e dataCadastro sao definidos pelo banco, nao pelo cliente.
            doador.setId(null);
            doador.setDataCadastro(null);

            Long idGerado = doadorService.inserir(doador);
            doador.setId(idGerado);
            resp.setHeader("Location", req.getContextPath()
                    + "/api/doadores/" + idGerado);
            responderJson(resp, HttpServletResponse.SC_CREATED, doador);
        } catch (JsonParseException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "JSON invalido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao cadastrar doador na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao cadastrar doador.");
        }
    }
}
