package br.com.mvc.controller.api;

import br.com.mvc.model.Doacao;
import br.com.mvc.service.DoacaoService;
import com.google.gson.JsonParseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;

/** Endpoints REST de doacoes, usando Service e DAO ja presentes na API. */
@WebServlet("/api/doacoes/*")
public class DoacaoApiController extends ApiServlet {

    private final DoacaoService doacaoService = new DoacaoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        try {
            if (caminho == null || "/".equals(caminho)) {
                responderJson(resp, HttpServletResponse.SC_OK, doacaoService.listar());
                return;
            }
            Long id = obterIdDaRota(req, resp, false);
            if (id == null) return;

            Doacao doacao = doacaoService.buscarPorId(id);
            if (doacao == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doacao nao encontrada.");
                return;
            }
            responderJson(resp, HttpServletResponse.SC_OK, doacao);
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao consultar doacoes na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao consultar doacoes.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String caminho = req.getPathInfo();
        if (caminho != null && !"/".equals(caminho)) {
            responderErro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "POST e permitido apenas em /api/doacoes.");
            return;
        }
        try {
            req.setCharacterEncoding("UTF-8");
            Doacao doacao = lerJson(req, Doacao.class);
            doacao.setId(null); // O ID vem do banco, nunca do JSON recebido.

            Long id = doacaoService.inserir(doacao);
            doacao.setId(id);
            resp.setHeader("Location", req.getContextPath() + "/api/doacoes/" + id);
            // Devolve a representacao persistida, com os dados do JOIN.
            Doacao cadastrada = doacaoService.buscarPorId(id);
            responderJson(resp, HttpServletResponse.SC_CREATED,
                    cadastrada != null ? cadastrada : doacao);
        } catch (JsonParseException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "JSON invalido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            responderErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao cadastrar doacao na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao cadastrar doacao.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long id = obterIdDaRota(req, resp, true);
        if (id == null) return;

        try {
            if (doacaoService.buscarPorId(id) == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doacao nao encontrada.");
                return;
            }
            req.setCharacterEncoding("UTF-8");
            Doacao doacao = lerJson(req, Doacao.class);
            doacao.setId(id); // O ID da URL prevalece sobre o do JSON.
            doacaoService.alterar(doacao);

            Doacao atualizada = doacaoService.buscarPorId(id);
            if (atualizada == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doacao nao encontrada apos atualizacao.");
                return;
            }
            responderJson(resp, HttpServletResponse.SC_OK, atualizada);
        } catch (JsonParseException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "JSON invalido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            responderErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            responderErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao atualizar doacao na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao atualizar doacao.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long id = obterIdDaRota(req, resp, true);
        if (id == null) return;

        try {
            if (doacaoService.buscarPorId(id) == null) {
                responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Doacao nao encontrada.");
                return;
            }
            doacaoService.deletar(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // Sem corpo de resposta.
        } catch (RuntimeException e) {
            getServletContext().log("Falha ao excluir doacao na API REST", e);
            responderErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno ao excluir doacao.");
        }
    }

    /** GET precisa de ID apenas na rota individual; PUT e DELETE sempre exigem. */
    private Long obterIdDaRota(HttpServletRequest req, HttpServletResponse resp,
            boolean obrigatorio) throws IOException {
        String caminho = req.getPathInfo();
        if (caminho == null || "/".equals(caminho)) {
            responderErro(resp, obrigatorio
                    ? HttpServletResponse.SC_METHOD_NOT_ALLOWED
                    : HttpServletResponse.SC_NOT_FOUND,
                    "Esta operacao exige /api/doacoes/{id}.");
            return null;
        }
        if (!caminho.matches("/[^/]+")) {
            responderErro(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Rota de doacoes nao encontrada.");
            return null;
        }
        try {
            Long id = Long.valueOf(caminho.substring(1));
            if (id > 0) return id;
        } catch (NumberFormatException e) {
            // Erro retornado abaixo.
        }
        responderErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                "O ID da doacao deve ser um numero inteiro positivo.");
        return null;
    }
}
