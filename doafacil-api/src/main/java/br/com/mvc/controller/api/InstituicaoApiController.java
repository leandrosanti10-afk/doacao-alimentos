package br.com.mvc.controller.api;

import br.com.mvc.model.Instituicao;
import br.com.mvc.service.InstituicaoService;
import com.google.gson.JsonParseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Endpoints REST de instituicoes: GET, POST, PUT e DELETE.
 * Mantem as regras de negocio no Service e a persistencia no DAO.
 */
@WebServlet("/api/instituicoes/*")
public class InstituicaoApiController extends ApiServlet {

    private final InstituicaoService instituicaoService = new InstituicaoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        try {
            if (caminho == null || "/".equals(caminho)) {
                responderJson(resp, HttpServletResponse.SC_OK, instituicaoService.listar());
                return;
            }

            // Apenas /api/instituicoes/{id}; caminhos com outras barras nao existem.
            if (!caminho.matches("/[^/]+")) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Rota de instituicoes nao encontrada.");
                return;
            }

            Long id;
            try {
                id = Long.valueOf(caminho.substring(1));
            } catch (NumberFormatException e) {
                responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "O ID da instituicao deve ser um numero inteiro positivo.");
                return;
            }
            if (id <= 0) {
                responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "O ID da instituicao deve ser um numero inteiro positivo.");
                return;
            }

            Instituicao instituicao = instituicaoService.buscarPorId(id);
            if (instituicao == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Instituicao nao encontrada.");
                return;
            }
            responderJson(resp, HttpServletResponse.SC_OK, instituicao);
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao consultar instituicoes na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao consultar instituicoes.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        if (caminho != null && !"/".equals(caminho)) {
            responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "POST e permitido apenas em /api/instituicoes.");
            return;
        }
        try {
            req.setCharacterEncoding("UTF-8");
            Instituicao instituicao = lerJson(req, Instituicao.class);
            // ID e dataCadastro sao definidos pelo banco, nao pelo cliente.
            instituicao.setId(null);
            instituicao.setDataCadastro(null);

            Long idGerado = instituicaoService.inserir(instituicao);
            instituicao.setId(idGerado);
            resp.setHeader("Location", req.getContextPath()
                    + "/api/instituicoes/" + idGerado);
            responderJson(resp, HttpServletResponse.SC_CREATED, instituicao);
        } catch (JsonParseException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "JSON invalido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao cadastrar instituicao na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao cadastrar instituicao.");
        }
    }


    /**
     * PUT /api/instituicoes/{id}: atualiza todos os campos editaveis da instituicao.
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
            if (instituicaoService.buscarPorId(id) == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Instituicao nao encontrada.");
                return;
            }

            req.setCharacterEncoding("UTF-8");
            Instituicao dados = lerJson(req, Instituicao.class);
            dados.setId(id); // Nao permitir alterar o ID pelo corpo da requisicao.
            dados.setDataCadastro(null); // A coluna nao sera atualizada pelo PUT.

            // Service reaproveita as validacoes e o DAO ja existentes.
            instituicaoService.alterar(dados);

            // Rele o registro para devolver a representacao persistida,
            // incluindo dataCadastro original.
            Instituicao atualizado = instituicaoService.buscarPorId(id);
            if (atualizado == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Instituicao nao encontrada apos a atualizacao.");
                return;
            }
            responderJson(resp, HttpServletResponse.SC_OK, atualizado);
        } catch (JsonParseException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "JSON invalido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao atualizar instituicao na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao atualizar instituicao.");
        }
    }

    /**
     * DELETE /api/instituicoes/{id}: remove somente se nao houver doacoes vinculadas.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long id = obterIdDaRota(req, resp);
        if (id == null) {
            return;
        }

        try {
            if (instituicaoService.buscarPorId(id) == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Instituicao nao encontrada.");
                return;
            }

            instituicaoService.deletar(id); // Service impede exclusao com doacoes.
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 sem corpo.
        } catch (IllegalStateException e) {
            responderErro(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao excluir instituicao na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao excluir instituicao.");
        }
    }

    /**
     * Valida /api/instituicoes/{id}, usado exclusivamente por PUT e DELETE.
     * Se nao houver ID, devolve 405; se invalido, devolve 400; se a rota
     * tiver segmentos adicionais, devolve 404.
     */
    private Long obterIdDaRota(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        if (caminho == null || "/".equals(caminho)) {
            responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Esta operacao exige a rota /api/instituicoes/{id}.");
            return null;
        }
        if (!caminho.matches("/[^/]+")) {
            responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Rota de instituicoes nao encontrada.");
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
                "O ID da instituicao deve ser um numero inteiro positivo.");
        return null;
    }
}
