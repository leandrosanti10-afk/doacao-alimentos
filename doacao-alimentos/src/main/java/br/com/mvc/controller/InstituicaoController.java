package br.com.mvc.controller;

import br.com.mvc.model.Instituicao;
import br.com.mvc.service.InstituicaoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/instituicao/*")
public class InstituicaoController extends HttpServlet {

    private final InstituicaoService instituicaoService =
            new InstituicaoService();

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String rota = extrairRota(req);

        switch (rota) {

            case "/listar":
                listar(req, resp);
                break;

            case "/inserir":
                mostrarFormularioNovo(req, resp);
                break;

            case "/alterar":
                mostrarFormularioEdicao(req, resp);
                break;

            case "/deletar":
                deletar(req, resp);
                break;

            default:
                listar(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String rota = extrairRota(req);

        switch (rota) {

            case "/inserir":
                salvarNovo(req, resp);
                break;

            case "/alterar":
                salvarAlteracao(req, resp);
                break;

            default:
                resp.sendRedirect(
                        req.getContextPath()
                                + "/instituicao/listar"
                );
                break;
        }
    }

    private String extrairRota(
            HttpServletRequest req) {

        String pathInfo =
                req.getPathInfo();

        if (pathInfo == null
                || pathInfo.isBlank()
                || "/".equals(pathInfo)) {

            return "/listar";
        }

        return pathInfo;
    }

    private void listar(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        List<Instituicao> instituicoes =
                instituicaoService.listar();

        req.setAttribute(
                "instituicoes",
                instituicoes
        );

        req.getRequestDispatcher(
                "/WEB-INF/instituicao/lista.jsp"
        ).forward(req, resp);
    }

    private void mostrarFormularioNovo(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute(
                "instituicao",
                new Instituicao()
        );

        req.setAttribute(
                "ehEdicao",
                false
        );

        req.getRequestDispatcher(
                "/WEB-INF/instituicao/form.jsp"
        ).forward(req, resp);
    }

    private void mostrarFormularioEdicao(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        Long id =
                Long.parseLong(
                        req.getParameter("id")
                );

        Instituicao instituicao =
                instituicaoService.buscarPorId(id);

        req.setAttribute(
                "instituicao",
                instituicao
        );

        req.setAttribute(
                "ehEdicao",
                true
        );

        req.getRequestDispatcher(
                "/WEB-INF/instituicao/form.jsp"
        ).forward(req, resp);
    }

    private void salvarNovo(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        String nome =
                req.getParameter("nome");

        String endereco =
                req.getParameter("endereco");

        Instituicao instituicao =
                new Instituicao();

        instituicao.setNome(nome);
        instituicao.setEndereco(endereco);

        instituicaoService.inserir(
                instituicao
        );

        resp.sendRedirect(
                req.getContextPath()
                        + "/instituicao/listar"
        );
    }

    private void salvarAlteracao(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        Long id =
                Long.parseLong(
                        req.getParameter("id")
                );

        String nome =
                req.getParameter("nome");

        String endereco =
                req.getParameter("endereco");

        Instituicao instituicao =
                new Instituicao();

        instituicao.setId(id);
        instituicao.setNome(nome);
        instituicao.setEndereco(endereco);

        instituicaoService.alterar(
                instituicao
        );

        resp.sendRedirect(
                req.getContextPath()
                        + "/instituicao/listar"
        );
    }

    private void deletar(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        Long id =
                Long.parseLong(
                        req.getParameter("id")
                );

        try {

            instituicaoService.deletar(id);

            resp.sendRedirect(
                    req.getContextPath()
                            + "/instituicao/listar"
            );

        } catch (IllegalStateException e) {

            req.setAttribute(
                    "erro",
                    e.getMessage()
            );

            listar(req, resp);
        }
    }
}