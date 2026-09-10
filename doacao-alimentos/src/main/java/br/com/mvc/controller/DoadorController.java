package br.com.mvc.controller;

import br.com.mvc.model.Doador;
import br.com.mvc.service.DoadorService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/doador/*")
public class DoadorController extends HttpServlet {

    private final DoadorService doadorService =
            new DoadorService();

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
                        + "/doador/listar"
                );
                break;
        }
    }

    private String extrairRota(HttpServletRequest req) {

    String pathInfo = req.getPathInfo();

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

        List<Doador> doadores =
                doadorService.listar();

        req.setAttribute(
                "doadores",
                doadores
        );

        req.getRequestDispatcher(
        "/WEB-INF/doador/lista.jsp"
).forward(req, resp);
    }

    private void mostrarFormularioNovo(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute(
                "doador",
                new Doador()
        );

        req.setAttribute(
                "ehEdicao",
                false
        );

        req.getRequestDispatcher(
        "/WEB-INF/doador/form.jsp"
).forward(req, resp);
    }

    private void mostrarFormularioEdicao(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam =
                req.getParameter("id");

        Long id =
                Long.parseLong(idParam);

        Doador doador =
                doadorService.buscarPorId(id);

        req.setAttribute(
                "doador",
                doador
        );

        req.setAttribute(
                "ehEdicao",
                true
        );

        req.getRequestDispatcher(
        "/WEB-INF/doador/form.jsp"
).forward(req, resp);
    }

    private void salvarNovo(
                HttpServletRequest req,
                HttpServletResponse resp)
                throws ServletException, IOException {

        Doador doador = new Doador();

        doador.setNome(
                req.getParameter("nome")
        );

        doador.setCpfCnpj(
                req.getParameter("cpfCnpj")
        );

        doador.setEmail(
                req.getParameter("email")
        );

        doador.setTelefone(
                req.getParameter("telefone")
        );

        doador.setCidade(
                req.getParameter("cidade")
        );

        try {

                doadorService.inserir(doador);

                resp.sendRedirect(
                        req.getContextPath()
                                + "/doador/listar"
                );

        } catch (IllegalArgumentException e) {

                req.setAttribute(
                        "erro",
                        e.getMessage()
                );

                req.setAttribute(
                        "doador",
                        doador
                );

                req.setAttribute(
                        "ehEdicao",
                        false
                );

                req.getRequestDispatcher(
                        "/WEB-INF/doador/form.jsp"
                ).forward(req, resp);
        }
        }

    private void salvarAlteracao(
                HttpServletRequest req,
                HttpServletResponse resp)
                throws ServletException, IOException {

        Doador doador =
                new Doador();

        doador.setId(
                Long.parseLong(
                        req.getParameter("id")
                )
        );

        doador.setNome(
                req.getParameter("nome")
        );

        doador.setCpfCnpj(
                req.getParameter("cpfCnpj")
        );

        doador.setEmail(
                req.getParameter("email")
        );

        doador.setTelefone(
                req.getParameter("telefone")
        );

        doador.setCidade(
                req.getParameter("cidade")
        );

        try {

                doadorService.alterar(doador);

                resp.sendRedirect(
                        req.getContextPath()
                                + "/doador/listar"
                );

        } catch (IllegalArgumentException e) {

                req.setAttribute(
                        "erro",
                        e.getMessage()
                );

                req.setAttribute(
                        "doador",
                        doador
                );

                req.setAttribute(
                        "ehEdicao",
                        true
                );

                req.getRequestDispatcher(
                        "/WEB-INF/doador/form.jsp"
                ).forward(req, resp);
        }
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

                doadorService.deletar(id);

                resp.sendRedirect(
                        req.getContextPath()
                                + "/doador/listar"
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