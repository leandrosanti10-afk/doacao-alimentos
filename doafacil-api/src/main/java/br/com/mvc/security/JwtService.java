package br.com.mvc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * Emissao e validacao de JWTs de curta duracao para o cliente Web.
 * A chave Base64 deve conter pelo menos 32 bytes aleatorios.
 */
public final class JwtService {
    private static final String EMISSOR = "doafacil-api";
    private static final long DURACAO_PADRAO_MIN = 30;
    private final SecretKey chave;
    private final long duracaoMinutos;

    public JwtService() {
        String codificada = System.getenv("JWT_SECRET_BASE64");
        if (codificada == null || codificada.isBlank()) {
            throw new IllegalStateException("JWT_SECRET_BASE64 nao configurada.");
        }
        byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(codificada);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("JWT_SECRET_BASE64 deve estar em Base64 valido.", ex);
        }
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET_BASE64 deve codificar pelo menos 32 bytes.");
        }
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.duracaoMinutos = obterDuracao();
    }

    private static long obterDuracao() {
        String configurada = System.getenv("JWT_TTL_MINUTES");
        if (configurada == null || configurada.isBlank()) {
            return DURACAO_PADRAO_MIN;
        }
        try {
            long minutos = Long.parseLong(configurada.trim());
            if (minutos < 1 || minutos > 60) {
                throw new IllegalArgumentException("JWT_TTL_MINUTES deve estar entre 1 e 60.");
            }
            return minutos;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("JWT_TTL_MINUTES deve ser um numero entre 1 e 60.", ex);
        }
    }

    public long getDuracaoSegundos() {
        return duracaoMinutos * 60;
    }

    public String emitir(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Usuario obrigatorio.");
        }
        Instant agora = Instant.now();
        return Jwts.builder()
                .issuer(EMISSOR)
                .subject(usuario)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusSeconds(getDuracaoSegundos())))
                .signWith(chave, Jwts.SIG.HS256)
                .compact();
    }

    /** Verifica assinatura, emissor, expiracao e usuario autorizado. */
    public String validar(String token) throws JwtException {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token ausente.");
        }
        Claims dados = Jwts.parser()
                .verifyWith(chave)
                .requireIssuer(EMISSOR)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String usuario = dados.getSubject();
        String usuarioPermitido = System.getenv("API_AUTH_USER");
        if (usuario == null || usuario.isBlank() || dados.getExpiration() == null
                || usuarioPermitido == null || !usuarioPermitido.equals(usuario)) {
            throw new IllegalArgumentException("Token nao autorizado.");
        }
        return usuario;
    }
}
