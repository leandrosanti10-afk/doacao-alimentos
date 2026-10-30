package br.com.mvc.dao;

import br.com.mvc.model.Instituicao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Persistencia de instituicoes com reutilizacao da infraestrutura BaseDAO. */
public class InstituicaoDAO extends BaseDAO {

    public void inserir(Instituicao instituicao) {
        String sql = "INSERT INTO instituicoes "
                + "(nome, endereco, telefone, email, cidade) VALUES (?, ?, ?, ?, ?)";
        executarAtualizacao(sql, ps -> {
            ps.setString(1, instituicao.getNome());
            ps.setString(2, instituicao.getEndereco());
            ps.setString(3, instituicao.getTelefone());
            ps.setString(4, instituicao.getEmail());
            ps.setString(5, instituicao.getCidade());
        }, "Erro ao inserir instituicao.");
    }

    public List<Instituicao> listar() {
        String sql = "SELECT id, nome, endereco, telefone, email, cidade, "
                + "data_cadastro FROM instituicoes ORDER BY data_cadastro DESC, id DESC";
        return executarConsulta(sql, null, rs -> {
            List<Instituicao> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(montarInstituicao(rs));
            }
            return lista;
        }, "Erro ao listar instituicoes.");
    }

    public Instituicao buscarPorId(Long id) {
        String sql = "SELECT id, nome, endereco, telefone, email, cidade, "
                + "data_cadastro FROM instituicoes WHERE id = ?";
        return executarConsulta(sql, ps -> ps.setLong(1, id),
                rs -> rs.next() ? montarInstituicao(rs) : null,
                "Erro ao buscar instituicao.");
    }

    public void alterar(Instituicao instituicao) {
        String sql = "UPDATE instituicoes SET nome = ?, endereco = ?, telefone = ?, "
                + "email = ?, cidade = ? WHERE id = ?";
        executarAtualizacao(sql, ps -> {
            ps.setString(1, instituicao.getNome());
            ps.setString(2, instituicao.getEndereco());
            ps.setString(3, instituicao.getTelefone());
            ps.setString(4, instituicao.getEmail());
            ps.setString(5, instituicao.getCidade());
            ps.setLong(6, instituicao.getId());
        }, "Erro ao alterar instituicao.");
    }

    public void deletar(Long id) {
        executarAtualizacao("DELETE FROM instituicoes WHERE id = ?",
                ps -> ps.setLong(1, id), "Erro ao excluir instituicao.");
    }

    public boolean possuiDoacoes(Long id) {
        return executarConsulta(
                "SELECT COUNT(*) FROM doacoes WHERE instituicao_id = ?",
                ps -> ps.setLong(1, id),
                rs -> rs.next() && rs.getInt(1) > 0,
                "Erro ao verificar doacoes da instituicao.");
    }

    public boolean existeEmail(String email, Long ignorarId) {
        return existeCampo("email", email, ignorarId,
                "Erro ao verificar email.");
    }

    public boolean existeTelefone(String telefone, Long ignorarId) {
        return existeCampo("telefone", telefone, ignorarId,
                "Erro ao verificar telefone.");
    }

    private boolean existeCampo(String coluna, String valor, Long ignorarId,
            String mensagemErro) {
        String sql = "SELECT COUNT(*) FROM instituicoes WHERE " + coluna
                + " = ? AND (? IS NULL OR id <> ?)";
        return executarConsulta(sql, ps -> {
            ps.setString(1, valor);
            prepararIdIgnorado(ps, ignorarId);
        }, rs -> rs.next() && rs.getInt(1) > 0, mensagemErro);
    }

    private Instituicao montarInstituicao(ResultSet rs) throws SQLException {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(rs.getLong("id"));
        instituicao.setNome(rs.getString("nome"));
        instituicao.setEndereco(rs.getString("endereco"));
        instituicao.setTelefone(rs.getString("telefone"));
        instituicao.setEmail(rs.getString("email"));
        instituicao.setCidade(rs.getString("cidade"));
        java.sql.Timestamp cadastro = rs.getTimestamp("data_cadastro");
        if (cadastro != null) {
            instituicao.setDataCadastro(cadastro.toLocalDateTime());
        }
        return instituicao;
    }
}
