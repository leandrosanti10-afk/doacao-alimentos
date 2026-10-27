package br.com.mvc.dao;

import br.com.mvc.model.Doador;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Persistencia de doadores; conexoes e recursos gerenciados pelo BaseDAO. */
public class DoadorDAO extends BaseDAO {

    public void inserir(Doador doador) {
        String sql = "INSERT INTO doadores "
                + "(nome, cpf_cnpj, email, telefone, cidade) "
                + "VALUES (?, ?, ?, ?, ?)";
        executarAtualizacao(sql, ps -> {
            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getCpfCnpj());
            ps.setString(3, doador.getEmail());
            ps.setString(4, doador.getTelefone());
            ps.setString(5, doador.getCidade());
        }, "Erro ao inserir doador.");
    }

    public List<Doador> listar() {
        String sql = "SELECT id, nome, cpf_cnpj, email, telefone, cidade, data_cadastro "
                + "FROM doadores ORDER BY data_cadastro DESC, id DESC";
        return executarConsulta(sql, null, rs -> {
            List<Doador> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(montarDoador(rs));
            }
            return lista;
        }, "Erro ao listar doadores.");
    }

    public Doador buscarPorId(Long id) {
        String sql = "SELECT id, nome, cpf_cnpj, email, telefone, cidade, data_cadastro "
                + "FROM doadores WHERE id = ?";
        return executarConsulta(sql, ps -> ps.setLong(1, id), rs ->
                rs.next() ? montarDoador(rs) : null,
                "Erro ao buscar doador.");
    }

    public void alterar(Doador doador) {
        String sql = "UPDATE doadores SET nome = ?, cpf_cnpj = ?, email = ?, "
                + "telefone = ?, cidade = ? WHERE id = ?";
        executarAtualizacao(sql, ps -> {
            ps.setString(1, doador.getNome());
            ps.setString(2, doador.getCpfCnpj());
            ps.setString(3, doador.getEmail());
            ps.setString(4, doador.getTelefone());
            ps.setString(5, doador.getCidade());
            ps.setLong(6, doador.getId());
        }, "Erro ao alterar doador.");
    }

    public void deletar(Long id) {
        executarAtualizacao("DELETE FROM doadores WHERE id = ?",
                ps -> ps.setLong(1, id), "Erro ao excluir doador.");
    }

    public boolean possuiDoacoes(Long id) {
        return executarConsulta("SELECT COUNT(*) FROM doacoes WHERE doador_id = ?",
                ps -> ps.setLong(1, id),
                rs -> rs.next() && rs.getInt(1) > 0,
                "Erro ao verificar doacoes do doador.");
    }

    public boolean existeCpfCnpj(String cpfCnpj, Long ignorarId) {
        return existeCampo("cpf_cnpj", cpfCnpj, ignorarId,
                "Erro ao verificar CPF/CNPJ.");
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
        // coluna e passada somente por metodos privados acima, nunca por entrada HTTP.
        String sql = "SELECT COUNT(*) FROM doadores WHERE " + coluna
                + " = ? AND (? IS NULL OR id <> ?)";
        return executarConsulta(sql, ps -> {
            ps.setString(1, valor);
            prepararIdIgnorado(ps, ignorarId);
        }, rs -> rs.next() && rs.getInt(1) > 0, mensagemErro);
    }

    private Doador montarDoador(ResultSet rs) throws SQLException {
        Doador doador = new Doador();
        doador.setId(rs.getLong("id"));
        doador.setNome(rs.getString("nome"));
        doador.setCpfCnpj(rs.getString("cpf_cnpj"));
        doador.setEmail(rs.getString("email"));
        doador.setTelefone(rs.getString("telefone"));
        doador.setCidade(rs.getString("cidade"));
        java.sql.Timestamp cadastro = rs.getTimestamp("data_cadastro");
        if (cadastro != null) {
            doador.setDataCadastro(cadastro.toLocalDateTime());
        }
        return doador;
    }
}
