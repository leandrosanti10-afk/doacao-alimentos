package br.com.mvc.controller.api;

import br.com.mvc.model.Doador;
import br.com.mvc.service.DoadorService;
import com.google.gson.JsonParseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Endpoints REST de doadores: GET, POST, PUT e DELETE.
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


    /**
     * PUT /api/doadores/{id}: atualiza todos os campos editaveis do doador.
     * O ID valido sempre e o da URL, nunca o enviado no corpo JSON.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long id = obterIdDaRota(req, resp);
        if (id == null) {
            return; // O helper ja escreveu o erro HTTP apropriado.
        }

        try {
            // Diferencia um registro inexistente de um erro de validacao.
            if (doadorService.buscarPorId(id) == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doador nao encontrado.");
                return;
            }

            req.setCharacterEncoding("UTF-8");
            Doador dados = lerJson(req, Doador.class);
            dados.setId(id); // Nao permitir alterar o ID pelo corpo da requisicao.
            dados.setDataCadastro(null); // A coluna nao sera atualizada pelo PUT.

            // Service reaproveita as validacoes e o DAO ja existentes.
            doadorService.alterar(dados);

            // Rele o registro para devolver a representacao persistida,
            // incluindo dataCadastro original.
            Doador atualizado = doadorService.buscarPorId(id);
            if (atualizado == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doador nao encontrado apos a atualizacao.");
                return;
            }
            responderJson(resp, HttpServletResponse.SC_OK, atualizado);
        } catch (JsonParseException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "JSON invalido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao atualizar doador na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao atualizar doador.");
        }
    }

    /**
     * DELETE /api/doadores/{id}: remove somente se nao houver doacoes vinculadas.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long id = obterIdDaRota(req, resp);
        if (id == null) {
            return;
        }

        try {
            if (doadorService.buscarPorId(id) == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doador nao encontrado.");
                return;
            }

            doadorService.deletar(id); // Service impede exclusao com doacoes.
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 sem corpo.
        } catch (IllegalStateException e) {
            responderErro(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao excluir doador na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao excluir doador.");
        }
    }

    /**
     * Valida /api/doadores/{id}, usado exclusivamente por PUT e DELETE.
     * Se nao houver ID, devolve 405; se invalido, devolve 400; se a rota
     * tiver segmentos adicionais, devolve 404.
     */
    private Long obterIdDaRota(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        if (caminho == null || "/".equals(caminho)) {
            responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Esta operacao exige a rota /api/doadores/{id}.");
            return null;
        }
        if (!caminho.matches("/[^/]+")) {
            responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Rota de doadores nao encontrada.");
            return null;
        }

        try {
            Long id = Long.valueOf(caminho.substring(1));
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException e) {
            // Tratado abaixo como ID invalido.
        }
        responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                "O ID do doador deve ser um numero inteiro positivo.");
        return null;
    }
}
