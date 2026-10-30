package br.com.mvc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/** Gerencia um pequeno pool de conexoes, compartilhado pela aplicacao. */
public final class MysqlSingleton {

    // Configuracao exclusiva do processo da API. Nao coloque senha no repositorio.
    private static final String URL = obterVariavel(
            "DB_URL", "jdbc:mysql://localhost:3306/doacao_alimentos");
    private static final String USUARIO = obterVariavel("DB_USER", "doafacil_api");

    private static String obterVariavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor.trim();
    }

    private volatile HikariDataSource pool;

    private MysqlSingleton() {
        // Nao abre conexoes durante a inicializacao dos Controllers.
    }

    private static class InstanciaHolder {
        private static final MysqlSingleton INSTANCIA = new MysqlSingleton();
    }

    public static MysqlSingleton getInstancia() {
        return InstanciaHolder.INSTANCIA;
    }

    /**
     * Empresta uma conexao do pool. O chamador DEVE fecha-la
     * com try-with-resources para devolve-la ao pool.
     */
    public Connection getConexao() throws SQLException {
        String senha = System.getenv("DB_PASSWORD");
        if (senha == null || senha.isBlank()) {
            throw new SQLException("Variavel de ambiente DB_PASSWORD nao configurada.");
        }
        return obterPool(senha).getConnection();
    }

    private HikariDataSource obterPool(String senha) throws SQLException {
        HikariDataSource atual = pool;
        if (atual == null) {
            synchronized (this) {
                atual = pool;
                if (atual == null) {
                    HikariConfig configuracao = new HikariConfig();
                    configuracao.setJdbcUrl(URL);
                    configuracao.setUsername(USUARIO);
                    configuracao.setPassword(senha);
                    configuracao.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    configuracao.setPoolName("DoaFacil-MySQL");
                    configuracao.setMaximumPoolSize(5);
                    configuracao.setMinimumIdle(1);
                    configuracao.setConnectionTimeout(10000);
                    try {
                        atual = new HikariDataSource(configuracao);
                    } catch (RuntimeException e) {
                        throw new SQLException("Erro ao inicializar o pool MySQL.", e);
                    }
                    pool = atual;
                }
            }
        }
        return atual;
    }

    /** Chamado quando o Tomcat encerra ou recarrega a aplicacao. */
    public synchronized void fecharPool() {
        if (pool != null) {
            pool.close();
            pool = null;
        }
    }
}
