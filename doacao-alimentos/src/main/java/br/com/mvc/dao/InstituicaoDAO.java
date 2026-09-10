package br.com.mvc.dao;

import br.com.mvc.config.MysqlSingleton;
import br.com.mvc.model.Instituicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

public class InstituicaoDAO {

    public void inserir(Instituicao instituicao) {

        String sql =
                "INSERT INTO instituicoes "
                + "(nome, endereco, telefone, email, cidade) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, instituicao.getNome());
            ps.setString(2, instituicao.getEndereco());
            ps.setString(3, instituicao.getTelefone());
            ps.setString(4, instituicao.getEmail());
            ps.setString(5, instituicao.getCidade());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao inserir instituição.",
                    e
            );
        }
    }

    public List<Instituicao> listar() {

        String sql =
                "SELECT id, nome, endereco, telefone, "
                + "email, cidade, data_cadastro "
                + "FROM instituicoes "
                + "ORDER BY nome";

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

                lista.add(
                        montarInstituicao(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao listar instituições.",
                    e
            );
        }

        return lista;
    }

    public Instituicao buscarPorId(Long id) {

        String sql =
                "SELECT id, nome, endereco, telefone, "
                + "email, cidade, data_cadastro "
                + "FROM instituicoes "
                + "WHERE id = ?";

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

                return montarInstituicao(rs);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao buscar instituição.",
                    e
            );
        }

        return null;
    }

    public void alterar(
            Instituicao instituicao) {

        String sql =
                "UPDATE instituicoes SET "
                + "nome = ?, "
                + "endereco = ?, "
                + "telefone = ?, "
                + "email = ?, "
                + "cidade = ? "
                + "WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(
                    1,
                    instituicao.getNome()
            );

            ps.setString(
                    2,
                    instituicao.getEndereco()
            );

            ps.setString(
                    3,
                    instituicao.getTelefone()
            );

            ps.setString(
                    4,
                    instituicao.getEmail()
            );

            ps.setString(
                    5,
                    instituicao.getCidade()
            );

            ps.setLong(
                    6,
                    instituicao.getId()
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao alterar instituição.",
                    e
            );
        }
    }

    public void deletar(Long id) {

        String sql =
                "DELETE FROM instituicoes "
                + "WHERE id = ?";

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
                    "Erro ao excluir instituição.",
                    e
            );
        }
    }

    public boolean possuiDoacoes(Long id) {

        String sql =
                "SELECT COUNT(*) "
                + "FROM doacoes "
                + "WHERE instituicao_id = ?";

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

            return rs.next()
                    && rs.getInt(1) > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao verificar doações da instituição.",
                    e
            );
        }
    }

    public boolean existeEmail(
            String email,
            Long ignorarId) {

        String sql =
                "SELECT COUNT(*) "
                + "FROM instituicoes "
                + "WHERE email = ? "
                + "AND (? IS NULL OR id <> ?)";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, email);

            if (ignorarId == null) {

                ps.setNull(
                        2,
                        Types.BIGINT
                );

                ps.setNull(
                        3,
                        Types.BIGINT
                );

            } else {

                ps.setLong(
                        2,
                        ignorarId
                );

                ps.setLong(
                        3,
                        ignorarId
                );
            }

            ResultSet rs =
                    ps.executeQuery();

            return rs.next()
                    && rs.getInt(1) > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao verificar email.",
                    e
            );
        }
    }

    public boolean existeTelefone(
            String telefone,
            Long ignorarId) {

        String sql =
                "SELECT COUNT(*) "
                + "FROM instituicoes "
                + "WHERE telefone = ? "
                + "AND (? IS NULL OR id <> ?)";

        try {

            Connection conexao =
                    MysqlSingleton
                            .getInstancia()
                            .getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(
                    1,
                    telefone
            );

            if (ignorarId == null) {

                ps.setNull(
                        2,
                        Types.BIGINT
                );

                ps.setNull(
                        3,
                        Types.BIGINT
                );

            } else {

                ps.setLong(
                        2,
                        ignorarId
                );

                ps.setLong(
                        3,
                        ignorarId
                );
            }

            ResultSet rs =
                    ps.executeQuery();

            return rs.next()
                    && rs.getInt(1) > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao verificar telefone.",
                    e
            );
        }
    }

    private Instituicao montarInstituicao(
            ResultSet rs)
            throws SQLException {

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

        instituicao.setTelefone(
                rs.getString("telefone")
        );

        instituicao.setEmail(
                rs.getString("email")
        );

        instituicao.setCidade(
                rs.getString("cidade")
        );

        if (rs.getTimestamp(
                "data_cadastro") != null) {

            instituicao.setDataCadastro(
                    rs.getTimestamp(
                            "data_cadastro"
                    ).toLocalDateTime()
            );
        }

        return instituicao;
    }
}