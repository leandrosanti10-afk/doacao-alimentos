package br.com.mvc.dao;

import br.com.mvc.model.Doacao;
import br.com.mvc.model.Doador;
import br.com.mvc.model.Instituicao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Persistencia de doacoes; o JOIN e o mapeamento existente sao preservados. */
public class DoacaoDAO extends BaseDAO {

    private static final String CONSULTA_COM_JOIN =
            "SELECT d.id, d.descricao, d.data_doacao, "
            + "doa.id AS doador_id, doa.nome AS doador_nome, "
            + "doa.cpf_cnpj AS doador_cpf_cnpj, doa.email AS doador_email, "
            + "doa.telefone AS doador_telefone, doa.cidade AS doador_cidade, "
            + "i.id AS instituicao_id, i.nome AS instituicao_nome, "
            + "i.endereco AS instituicao_endereco, "
            + "i.telefone AS instituicao_telefone, "
            + "i.email AS instituicao_email, i.cidade AS instituicao_cidade "
            + "FROM doacoes d "
            + "JOIN doadores doa ON d.doador_id = doa.id "
            + "JOIN instituicoes i ON d.instituicao_id = i.id ";

    public Long inserir(Doacao doacao) {
        String sql = "INSERT INTO doacoes "
                + "(doador_id, instituicao_id, descricao, data_doacao) "
                + "VALUES (?, ?, ?, ?)";
        return executarInsercaoComId(sql, ps -> {
            ps.setLong(1, doacao.getDoador().getId());
            ps.setLong(2, doacao.getInstituicao().getId());
            ps.setString(3, doacao.getDescricao());
            ps.setDate(4, java.sql.Date.valueOf(doacao.getDataDoacao()));
        }, "Erro ao inserir doacao.");
    }

    public List<Doacao> listar() {
        String sql = CONSULTA_COM_JOIN
                + "ORDER BY d.data_doacao DESC, d.id DESC";
        return executarConsulta(sql, null, rs -> {
            List<Doacao> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(montarDoacao(rs));
            }
            return lista;
        }, "Erro ao listar doacoes.");
    }

    public Doacao buscarPorId(Long id) {
        String sql = CONSULTA_COM_JOIN + "WHERE d.id = ?";
        return executarConsulta(sql, ps -> ps.setLong(1, id),
                rs -> rs.next() ? montarDoacao(rs) : null,
                "Erro ao buscar doacao.");
    }

    public void alterar(Doacao doacao) {
        String sql = "UPDATE doacoes SET doador_id = ?, instituicao_id = ?, "
                + "descricao = ?, data_doacao = ? WHERE id = ?";
        executarAtualizacao(sql, ps -> {
            ps.setLong(1, doacao.getDoador().getId());
            ps.setLong(2, doacao.getInstituicao().getId());
            ps.setString(3, doacao.getDescricao());
            ps.setDate(4, java.sql.Date.valueOf(doacao.getDataDoacao()));
            ps.setLong(5, doacao.getId());
        }, "Erro ao alterar doacao.");
    }

    public void deletar(Long id) {
        executarAtualizacao("DELETE FROM doacoes WHERE id = ?",
                ps -> ps.setLong(1, id), "Erro ao excluir doacao.");
    }

    private Doacao montarDoacao(ResultSet rs) throws SQLException {
        Doador doador = new Doador();
        doador.setId(rs.getLong("doador_id"));
        doador.setNome(rs.getString("doador_nome"));
        doador.setCpfCnpj(rs.getString("doador_cpf_cnpj"));
        doador.setEmail(rs.getString("doador_email"));
        doador.setTelefone(rs.getString("doador_telefone"));
        doador.setCidade(rs.getString("doador_cidade"));

        Instituicao instituicao = new Instituicao();
        instituicao.setId(rs.getLong("instituicao_id"));
        instituicao.setNome(rs.getString("instituicao_nome"));
        instituicao.setEndereco(rs.getString("instituicao_endereco"));
        instituicao.setTelefone(rs.getString("instituicao_telefone"));
        instituicao.setEmail(rs.getString("instituicao_email"));
        instituicao.setCidade(rs.getString("instituicao_cidade"));

        Doacao doacao = new Doacao();
        doacao.setId(rs.getLong("id"));
        doacao.setDoador(doador);
        doacao.setInstituicao(instituicao);
        doacao.setDescricao(rs.getString("descricao"));
        doacao.setDataDoacao(rs.getDate("data_doacao").toLocalDate());
        return doacao;
    }
}
