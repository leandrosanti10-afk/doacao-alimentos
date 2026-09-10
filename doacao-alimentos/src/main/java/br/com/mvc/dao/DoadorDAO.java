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
                "INSERT INTO doadores "
                + "(nome, cpf_cnpj, email, telefone, cidade) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {

            Connection conexao =
                    MysqlSingleton.getInstancia().getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getCpfCnpj());
            ps.setString(3, doador.getEmail());
            ps.setString(4, doador.getTelefone());
            ps.setString(5, doador.getCidade());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao inserir doador.",
                    e
            );
        }
    }

    public List<Doador> listar() {

        String sql =
                "SELECT id, nome, cpf_cnpj, email, telefone, cidade, data_cadastro "
                + "FROM doadores ORDER BY nome";

        List<Doador> lista =
                new ArrayList<>();

        try {

            Connection conexao =
                    MysqlSingleton.getInstancia().getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Doador doador =
                        montarDoador(rs);

                lista.add(doador);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao listar doadores.",
                    e
            );
        }

        return lista;
    }

    public Doador buscarPorId(Long id) {

        String sql =
                "SELECT id, nome, cpf_cnpj, email, telefone, cidade, data_cadastro "
                + "FROM doadores WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton.getInstancia().getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setLong(1, id);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {
                return montarDoador(rs);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao buscar doador.",
                    e
            );
        }

        return null;
    }

    public void alterar(Doador doador) {

        String sql =
                "UPDATE doadores SET "
                + "nome = ?, "
                + "cpf_cnpj = ?, "
                + "email = ?, "
                + "telefone = ?, "
                + "cidade = ? "
                + "WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton.getInstancia().getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getCpfCnpj());
            ps.setString(3, doador.getEmail());
            ps.setString(4, doador.getTelefone());
            ps.setString(5, doador.getCidade());
            ps.setLong(6, doador.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao alterar doador.",
                    e
            );
        }
    }

    public void deletar(Long id) {

        String sql =
                "DELETE FROM doadores WHERE id = ?";

        try {

            Connection conexao =
                    MysqlSingleton.getInstancia().getConexao();

            PreparedStatement ps =
                    conexao.prepareStatement(sql);

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao excluir doador.",
                    e
            );
        }
    }

    public boolean possuiDoacoes(Long id) {

        String sql =
                "SELECT COUNT(*) FROM doacoes WHERE doador_id = ?";

        try {

            Connection conexao =
                    MysqlSingleton.getInstancia().getConexao();

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
                    "Erro ao verificar doações do doador.",
                    e
            );
        }

        return false;
    }

    private Doador montarDoador(ResultSet rs)
            throws SQLException {

        Doador doador =
                new Doador();

        doador.setId(
                rs.getLong("id")
        );

        doador.setNome(
                rs.getString("nome")
        );

        doador.setCpfCnpj(
                rs.getString("cpf_cnpj")
        );

        doador.setEmail(
                rs.getString("email")
        );

        doador.setTelefone(
                rs.getString("telefone")
        );

        doador.setCidade(
                rs.getString("cidade")
        );

        if (rs.getTimestamp("data_cadastro") != null) {

            doador.setDataCadastro(
                    rs.getTimestamp("data_cadastro")
                            .toLocalDateTime()
            );
        }

        return doador;
    }

    public boolean existeCpfCnpj(String cpfCnpj, Long ignorarId) {

        String sql =
                "SELECT COUNT(*) FROM doadores "
                + "WHERE cpf_cnpj = ? "
                + "AND (? IS NULL OR id <> ?)";

        try {

                Connection conexao =
                        MysqlSingleton.getInstancia().getConexao();

                PreparedStatement ps =
                        conexao.prepareStatement(sql);

                ps.setString(1, cpfCnpj);

                if (ignorarId == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
                ps.setNull(3, java.sql.Types.BIGINT);
                } else {
                ps.setLong(2, ignorarId);
                ps.setLong(3, ignorarId);
                }

                ResultSet rs = ps.executeQuery();

                return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
                throw new RuntimeException(
                        "Erro ao verificar CPF/CNPJ.",
                        e
                );
        }
        }

        public boolean existeEmail(String email, Long ignorarId) {

        String sql =
                "SELECT COUNT(*) FROM doadores "
                + "WHERE email = ? "
                + "AND (? IS NULL OR id <> ?)";

        try {

                Connection conexao =
                        MysqlSingleton.getInstancia().getConexao();

                PreparedStatement ps =
                        conexao.prepareStatement(sql);

                ps.setString(1, email);

                if (ignorarId == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
                ps.setNull(3, java.sql.Types.BIGINT);
                } else {
                ps.setLong(2, ignorarId);
                ps.setLong(3, ignorarId);
                }

                ResultSet rs = ps.executeQuery();

                return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
                throw new RuntimeException(
                        "Erro ao verificar email.",
                        e
                );
        }
        }

        public boolean existeTelefone(String telefone, Long ignorarId) {

        String sql =
                "SELECT COUNT(*) FROM doadores "
                + "WHERE telefone = ? "
                + "AND (? IS NULL OR id <> ?)";

        try {

                Connection conexao =
                        MysqlSingleton.getInstancia().getConexao();

                PreparedStatement ps =
                        conexao.prepareStatement(sql);

                ps.setString(1, telefone);

                if (ignorarId == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
                ps.setNull(3, java.sql.Types.BIGINT);
                } else {
                ps.setLong(2, ignorarId);
                ps.setLong(3, ignorarId);
                }

                ResultSet rs = ps.executeQuery();

                return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
                throw new RuntimeException(
                        "Erro ao verificar telefone.",
                        e
                );
        }
        }
}