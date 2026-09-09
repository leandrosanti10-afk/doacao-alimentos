package br.com.mvc.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlSingleton {

    private static final String URL =
            "jdbc:mysql://localhost:3306/doacao_alimentos";

    private static final String USER = "root";

    private static final String PASSWORD =
            "root1895";

    private static MysqlSingleton instancia;

    private Connection conexao;

    private MysqlSingleton() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(
            "Driver MySQL não encontrado.",
            e
        );
    }
}

    public static MysqlSingleton getInstancia() {

        if (instancia == null) {
            instancia = new MysqlSingleton();
        }

        return instancia;
    }

    public Connection getConexao() throws SQLException {

        if (conexao == null || conexao.isClosed()) {

            conexao = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
        }

        return conexao;
    }
}