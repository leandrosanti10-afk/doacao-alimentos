package br.com.mvc.security;

import br.com.mvc.util.ApiJson;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Protege toda a API por padrao; libera somente status e login. */
@WebFilter(urlPatterns = {"/api/*"})
public class JwtAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String rota = req.getServletPath();

        if ("/api/status".equals(rota) || "/api/auth/login".equals(rota)) {
            chain.doFilter(request, response);
            return;
        }

        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Vary", "Authorization");
        String autorizacao = req.getHeader("Authorization");
        if (autorizacao == null || !autorizacao.regionMatches(true, 0, "Bearer ", 0, 7)) {
            negar(resp);
            return;
        }
        String token = autorizacao.substring(7).trim();
        if (!token.matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+")) {
            negar(resp);
            return;
        }

        final String usuario;
        try {
            usuario = new JwtService().validar(token);
        } catch (JwtException | IllegalArgumentException ex) {
            negar(resp);
            return;
        } catch (IllegalStateException ex) {
            ApiJson.erro(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Autenticacao indisponivel.");
            return;
        }
        req.setAttribute("usuarioAutenticado", usuario);
        chain.doFilter(request, response);
    }

    private static void negar(HttpServletResponse resp) throws IOException {
        resp.setHeader("WWW-Authenticate", "Bearer realm=\"DoaFacil API\"");
        ApiJson.erro(resp, HttpServletResponse.SC_UNAUTHORIZED,
                "Token ausente, invalido ou expirado.");
    }
}
