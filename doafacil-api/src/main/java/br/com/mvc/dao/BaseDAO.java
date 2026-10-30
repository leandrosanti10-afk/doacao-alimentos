package br.com.mvc.dao;

import br.com.mvc.config.MysqlSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/** Centraliza conexao, parametros SQL, execucao e fechamento de recursos JDBC. */
public abstract class BaseDAO {

    // Obtem a referencia ao gerenciador uma unica vez por instancia de DAO.
    private final MysqlSingleton conexoes = MysqlSingleton.getInstancia();

    @FunctionalInterface
    protected interface Parametros {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    protected interface Resultado<T> {
        T ler(ResultSet rs) throws SQLException;
    }

    protected int executarAtualizacao(
            String sql, Parametros parametros, String mensagemErro) {
        try (Connection conexao = conexoes.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (parametros != null) {
                parametros.aplicar(ps);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(mensagemErro, e);
        }
    }

    /**
     * Executa INSERT e devolve a chave primaria gerada pelo MySQL.
     * A conexao volta ao pool mesmo quando ocorre erro.
     */
    protected Long executarInsercaoComId(
            String sql, Parametros parametros, String mensagemErro) {
        try (Connection conexao = conexoes.getConexao();
             PreparedStatement ps = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            if (parametros != null) {
                parametros.aplicar(ps);
            }
            int linhas = ps.executeUpdate();
            if (linhas != 1) {
                throw new SQLException("A insercao nao afetou exatamente uma linha.");
            }
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getLong(1);
                }
            }
            throw new SQLException("O banco nao retornou o ID gerado.");
        } catch (SQLException e) {
            throw new RuntimeException(mensagemErro, e);
        }
    }

    protected <T> T executarConsulta(
            String sql, Parametros parametros, Resultado<T> resultado,
            String mensagemErro) {
        try (Connection conexao = conexoes.getConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (parametros != null) {
                parametros.aplicar(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return resultado.ler(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(mensagemErro, e);
        }
    }

    /** Para evitar repetir o tratamento de IDs opcionais nas verificacoes de duplicidade. */
    protected void prepararIdIgnorado(PreparedStatement ps, Long ignorarId)
            throws SQLException {
        if (ignorarId == null) {
            ps.setNull(2, java.sql.Types.BIGINT);
            ps.setNull(3, java.sql.Types.BIGINT);
        } else {
            ps.setLong(2, ignorarId);
            ps.setLong(3, ignorarId);
        }
    }
}
