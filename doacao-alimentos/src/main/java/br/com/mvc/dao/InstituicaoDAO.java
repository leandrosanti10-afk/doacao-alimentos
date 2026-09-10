package br.com.mvc.dao;

import br.com.mvc.config.MysqlSingleton;
import br.com.mvc.model.Instituicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class InstituicaoDAO {

    public void inserir(Instituicao instituicao) {

        String sql =
                "INSERT INTO instituicoes (nome, endereco) VALUES (?, ?)";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, instituicao.getNome());
            ps.setString(2, instituicao.getEndereco());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao inserir instituição",
                    e
            );
        }
    }

    public List<Instituicao> listar() {

        String sql =
                "SELECT id, nome, endereco FROM instituicoes";

        List<Instituicao> lista =
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

                Instituicao instituicao =
                        new Instituicao();

                instituicao.setId(
                        rs.getLong("id")
                );

                instituicao.setNome(
                        rs.getString("nome")
                );

                instituicao.setEndereco(
                        rs.getString("endereco")
                );

                lista.add(instituicao);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao listar instituições",
                    e
            );
        }

        return lista;
    }

    public Instituicao buscarPorId(Long id) {

        String sql =
                "SELECT id, nome, endereco FROM instituicoes WHERE id = ?";

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

                Instituicao instituicao =
                        new Instituicao();

                instituicao.setId(
                        rs.getLong("id")
                );

                instituicao.setNome(
                        rs.getString("nome")
                );

                instituicao.setEndereco(
                        rs.getString("endereco")
                );

                return instituicao;
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao buscar instituição",
                    e
            );
        }

        return null;
    }

    public void alterar(Instituicao instituicao) {

        String sql =
                "UPDATE instituicoes SET nome = ?, endereco = ? WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, instituicao.getNome());
            ps.setString(2, instituicao.getEndereco());
            ps.setLong(3, instituicao.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao alterar instituição",
                    e
            );
        }
    }

    public void deletar(Long id) {

        String sql =
                "DELETE FROM instituicoes WHERE id = ?";

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
                    "Erro ao deletar instituição",
                    e
            );
        }
    }

    public boolean possuiDoacoes(Long id) {

        String sql =
                "SELECT COUNT(*) FROM doacoes WHERE instituicao_id = ?";

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
                return rs.getInt(1) > 0;
                }

        } catch (SQLException e) {

                throw new RuntimeException(
                        "Erro ao verificar doações da instituição",
                        e
                );
        }

        return false;
        }
}