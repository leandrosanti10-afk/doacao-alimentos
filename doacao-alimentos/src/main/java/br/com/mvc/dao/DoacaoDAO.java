package br.com.mvc.dao;

import br.com.mvc.config.MysqlSingleton;
import br.com.mvc.model.Doacao;
import br.com.mvc.model.Doador;
import br.com.mvc.model.Instituicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class DoacaoDAO {

    public void inserir(Doacao doacao) {

        String sql =
                "INSERT INTO doacoes "
                + "(doador_id, instituicao_id, descricao, data_doacao) "
                + "VALUES (?, ?, ?, ?)";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setLong(
                    1,
                    doacao.getDoador().getId()
            );

            ps.setLong(
                    2,
                    doacao.getInstituicao().getId()
            );

            ps.setString(
                    3,
                    doacao.getDescricao()
            );

            ps.setDate(
                    4,
                    java.sql.Date.valueOf(
                            doacao.getDataDoacao()
                    )
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao inserir doação",
                    e
            );
        }
    }

    public List<Doacao> listar() {

        String sql =
                "SELECT "
                + "d.id, "
                + "d.descricao, "
                + "d.data_doacao, "
                + "doa.id AS doador_id, "
                + "doa.nome AS doador_nome, "
                + "doa.email AS doador_email, "
                + "i.id AS instituicao_id, "
                + "i.nome AS instituicao_nome, "
                + "i.endereco AS instituicao_endereco "
                + "FROM doacoes d "
                + "JOIN doadores doa "
                + "ON d.doador_id = doa.id "
                + "JOIN instituicoes i "
                + "ON d.instituicao_id = i.id";

        List<Doacao> lista =
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
                        rs.getLong("doador_id")
                );

                doador.setNome(
                        rs.getString("doador_nome")
                );

                doador.setEmail(
                        rs.getString("doador_email")
                );

                Instituicao instituicao =
                        new Instituicao();

                instituicao.setId(
                        rs.getLong("instituicao_id")
                );

                instituicao.setNome(
                        rs.getString("instituicao_nome")
                );

                instituicao.setEndereco(
                        rs.getString("instituicao_endereco")
                );

                Doacao doacao =
                        new Doacao();

                doacao.setId(
                        rs.getLong("id")
                );

                doacao.setDescricao(
                        rs.getString("descricao")
                );

                doacao.setDataDoacao(
                        rs.getDate("data_doacao")
                                .toLocalDate()
                );

                doacao.setDoador(doador);

                doacao.setInstituicao(
                        instituicao
                );

                lista.add(doacao);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao listar doações",
                    e
            );
        }

        return lista;
    }

    public Doacao buscarPorId(Long id) {

        String sql =
                "SELECT "
                + "d.id, "
                + "d.descricao, "
                + "d.data_doacao, "
                + "doa.id AS doador_id, "
                + "doa.nome AS doador_nome, "
                + "doa.email AS doador_email, "
                + "i.id AS instituicao_id, "
                + "i.nome AS instituicao_nome, "
                + "i.endereco AS instituicao_endereco "
                + "FROM doacoes d "
                + "JOIN doadores doa "
                + "ON d.doador_id = doa.id "
                + "JOIN instituicoes i "
                + "ON d.instituicao_id = i.id "
                + "WHERE d.id = ?";

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
                        rs.getLong("doador_id")
                );

                doador.setNome(
                        rs.getString("doador_nome")
                );

                doador.setEmail(
                        rs.getString("doador_email")
                );

                Instituicao instituicao =
                        new Instituicao();

                instituicao.setId(
                        rs.getLong("instituicao_id")
                );

                instituicao.setNome(
                        rs.getString("instituicao_nome")
                );

                instituicao.setEndereco(
                        rs.getString("instituicao_endereco")
                );

                Doacao doacao =
                        new Doacao();

                doacao.setId(
                        rs.getLong("id")
                );

                doacao.setDescricao(
                        rs.getString("descricao")
                );

                doacao.setDataDoacao(
                        rs.getDate("data_doacao")
                                .toLocalDate()
                );

                doacao.setDoador(doador);

                doacao.setInstituicao(
                        instituicao
                );

                return doacao;
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao buscar doação",
                    e
            );
        }

        return null;
    }

    public void alterar(Doacao doacao) {

        String sql =
                "UPDATE doacoes SET "
                + "doador_id = ?, "
                + "instituicao_id = ?, "
                + "descricao = ?, "
                + "data_doacao = ? "
                + "WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setLong(
                    1,
                    doacao.getDoador().getId()
            );

            ps.setLong(
                    2,
                    doacao.getInstituicao().getId()
            );

            ps.setString(
                    3,
                    doacao.getDescricao()
            );

            ps.setDate(
                    4,
                    java.sql.Date.valueOf(
                            doacao.getDataDoacao()
                    )
            );

            ps.setLong(
                    5,
                    doacao.getId()
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao alterar doação",
                    e
            );
        }
    }

    public void deletar(Long id) {

        String sql =
                "DELETE FROM doacoes WHERE id = ?";

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
                    "Erro ao deletar doação",
                    e
            );
        }
    }
}