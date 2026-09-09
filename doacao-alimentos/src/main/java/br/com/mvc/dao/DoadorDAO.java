package br.com.mvc.dao;

import br.com.mvc.config.MysqlSingleton;
import br.com.mvc.model.Doador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class DoadorDAO {

    public void inserir(Doador doador) {

        String sql =
                "INSERT INTO doadores (nome, email) VALUES (?, ?)";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getEmail());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao inserir doador",
                    e
            );
        }
    }

    public List<Doador> listar() {

        String sql =
                "SELECT id, nome, email FROM doadores";

        List<Doador> lista =
                new ArrayList<>();

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Doador doador =
                        new Doador();

                doador.setId(
                        rs.getLong("id")
                );

                doador.setNome(
                        rs.getString("nome")
                );

                doador.setEmail(
                        rs.getString("email")
                );

                lista.add(doador);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao listar doadores",
                    e
            );
        }

        return lista;
    }

    public Doador buscarPorId(Long id) {

        String sql =
                "SELECT id, nome, email FROM doadores WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setLong(1, id);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                Doador doador =
                        new Doador();

                doador.setId(
                        rs.getLong("id")
                );

                doador.setNome(
                        rs.getString("nome")
                );

                doador.setEmail(
                        rs.getString("email")
                );

                return doador;
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao buscar doador",
                    e
            );
        }

        return null;
    }

    public void alterar(Doador doador) {

        String sql =
                "UPDATE doadores SET nome = ?, email = ? WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getEmail());
            ps.setLong(3, doador.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao alterar doador",
                    e
            );
        }
    }

    public void deletar(Long id) {

        String sql =
                "DELETE FROM doadores WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao deletar doador",
                    e
            );
        }
    }
}