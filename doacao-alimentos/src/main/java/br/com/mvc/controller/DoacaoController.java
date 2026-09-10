package br.com.mvc.controller;

import br.com.mvc.model.Doacao;
import br.com.mvc.model.Doador;
import br.com.mvc.model.Instituicao;

import br.com.mvc.service.DoacaoService;
import br.com.mvc.service.DoadorService;
import br.com.mvc.service.InstituicaoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/doacao/*")
public class DoacaoController extends HttpServlet {

    private final DoacaoService doacaoService =
            new DoacaoService();

    private final DoadorService doadorService =
            new DoadorService();

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
                                + "/doacao/listar"
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

        List<Doacao> doacoes =
                doacaoService.listar();

        req.setAttribute(
                "doacoes",
                doacoes
        );

        req.getRequestDispatcher(
                "/WEB-INF/doacao/lista.jsp"
        ).forward(req, resp);
    }

    private void mostrarFormularioNovo(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        Doacao doacao =
                new Doacao();

        carregarListas(req);

        req.setAttribute(
                "doacao",
                doacao
        );

        req.setAttribute(
                "ehEdicao",
                false
        );

        req.getRequestDispatcher(
                "/WEB-INF/doacao/form.jsp"
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

        Doacao doacao =
                doacaoService.buscarPorId(id);

        carregarListas(req);

        req.setAttribute(
                "doacao",
                doacao
        );

        req.setAttribute(
                "ehEdicao",
                true
        );

        req.getRequestDispatcher(
                "/WEB-INF/doacao/form.jsp"
        ).forward(req, resp);
    }

    private void salvarNovo(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        Long doadorId =
                Long.parseLong(
                        req.getParameter("doadorId")
                );

        Long instituicaoId =
                Long.parseLong(
                        req.getParameter("instituicaoId")
                );

        String descricao =
                req.getParameter("descricao");

        LocalDate dataDoacao =
                LocalDate.parse(
                        req.getParameter("dataDoacao")
                );

        Doador doador =
                doadorService.buscarPorId(doadorId);

        Instituicao instituicao =
                instituicaoService.buscarPorId(
                        instituicaoId
                );

        Doacao doacao =
                new Doacao();

        doacao.setDoador(doador);

        doacao.setInstituicao(
                instituicao
        );

        doacao.setDescricao(
                descricao
        );

        doacao.setDataDoacao(
                dataDoacao
        );

        doacaoService.inserir(
                doacao
        );

        resp.sendRedirect(
                req.getContextPath()
                        + "/doacao/listar"
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

        Long doadorId =
                Long.parseLong(
                        req.getParameter("doadorId")
                );

        Long instituicaoId =
                Long.parseLong(
                        req.getParameter("instituicaoId")
                );

        String descricao =
                req.getParameter("descricao");

        LocalDate dataDoacao =
                LocalDate.parse(
                        req.getParameter("dataDoacao")
                );

        Doador doador =
                doadorService.buscarPorId(
                        doadorId
                );

        Instituicao instituicao =
                instituicaoService.buscarPorId(
                        instituicaoId
                );

        Doacao doacao =
                new Doacao();

        doacao.setId(id);

        doacao.setDoador(
                doador
        );

        doacao.setInstituicao(
                instituicao
        );

        doacao.setDescricao(
                descricao
        );

        doacao.setDataDoacao(
                dataDoacao
        );

        doacaoService.alterar(
                doacao
        );

        resp.sendRedirect(
                req.getContextPath()
                        + "/doacao/listar"
        );
    }

    private void deletar(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        Long id =
                Long.parseLong(
                        req.getParameter("id")
                );

        doacaoService.deletar(id);

        resp.sendRedirect(
                req.getContextPath()
                        + "/doacao/listar"
        );
    }

    private void carregarListas(
            HttpServletRequest req) {

        List<Doador> doadores =
                doadorService.listar();

        List<Instituicao> instituicoes =
                instituicaoService.listar();

        req.setAttribute(
                "doadores",
                doadores
        );

        req.setAttribute(
                "instituicoes",
                instituicoes
        );
    }
}