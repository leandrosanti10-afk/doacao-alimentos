package br.com.mvc.api;

import br.com.mvc.dao.InstituicaoDAO;
import br.com.mvc.model.Doacao;
import br.com.mvc.model.Doador;
import br.com.mvc.model.Instituicao;
import br.com.mvc.service.DoacaoService;
import br.com.mvc.service.DoadorService;
import br.com.mvc.service.InstituicaoService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Testa as camadas Service/DAO Web contra a API REST real.
 * Nao altera o doador escolhido; cria somente uma instituicao e uma doacao
 * temporarias e tenta exclui-las em finally, mesmo quando um teste falha.
 */
public final class InstituicaoDoacaoHttpSmokeTest {
    private InstituicaoDoacaoHttpSmokeTest() { }

    public static void main(String[] args) {
        InstituicaoService instituicoes = new InstituicaoService();
        DoacaoService doacoes = new DoacaoService();
        DoadorService doadores = new DoadorService();
        InstituicaoDAO instituicaoDao = new InstituicaoDAO();
        Long instituicaoId = null;
        Long doacaoId = null;
        boolean sucesso = false;
        try {
            List<Instituicao> listaInstituicoes = instituicoes.listar();
            List<Doacao> listaDoacoes = doacoes.listar();
            if (listaInstituicoes == null || listaDoacoes == null) {
                throw new IllegalStateException("A API retornou uma listagem nula.");
            }
            System.out.println("APROVADO: GET instituicoes e doacoes via HTTP + JWT");

            // Referencia somente de leitura; nenhum dado desse doador sera alterado.
            Doador doadorReferencia = doadores.listar().stream()
                    .filter(d -> d != null && d.getId() != null && d.getId() > 0)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Nao existe doador para referenciar a doacao temporaria."));
            System.out.println("Referencia somente leitura: doador ID " + doadorReferencia.getId());

            long aleatorio = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
            String sufixo = Long.toString(aleatorio);
            Instituicao instituicao = new Instituicao();
            instituicao.setNome("Instituicao Temporaria Web HTTP");
            instituicao.setEndereco("Rua de Testes, 100");
            instituicao.setTelefone("349" + sufixo.substring(0, 8));
            instituicao.setEmail("instituicao.web." + sufixo + "@example.com");
            instituicao.setCidade("Uberaba");

            // DAO preserva o retorno void, mas preenche o ID do objeto criado.
            instituicoes.inserir(instituicao);
            instituicaoId = instituicao.getId();
            if (instituicaoId == null || instituicaoId <= 0) {
                throw new IllegalStateException("Instituicao inserida sem ID.");
            }
            System.out.println("APROVADO: POST instituicao temporaria ID " + instituicaoId);

            Instituicao salva = instituicoes.buscarPorId(instituicaoId);
            if (salva == null || !instituicao.getEmail().equals(salva.getEmail())) {
                throw new IllegalStateException("GET instituicao temporaria nao corresponde ao POST.");
            }
            System.out.println("APROVADO: GET instituicao temporaria por ID");

            if (!instituicaoDao.existeEmail(instituicao.getEmail(), null)
                    || instituicaoDao.existeEmail(instituicao.getEmail(), instituicaoId)) {
                throw new IllegalStateException("Verificacao HTTP de email duplicado falhou.");
            }
            System.out.println("APROVADO: checagem de duplicidade via REST");

            salva.setCidade("Ribeirao Preto");
            instituicoes.alterar(salva);
            Instituicao atualizada = instituicoes.buscarPorId(instituicaoId);
            if (atualizada == null || !"Ribeirao Preto".equals(atualizada.getCidade())) {
                throw new IllegalStateException("PUT da instituicao nao persistiu.");
            }
            System.out.println("APROVADO: PUT instituicao e GET confirmacao");

            Doacao doacao = new Doacao();
            doacao.setDoador(doadorReferencia);
            doacao.setInstituicao(atualizada);
            doacao.setDescricao("Doacao temporaria do teste Web HTTP");
            doacao.setDataDoacao(LocalDate.now());
            doacoes.inserir(doacao);
            doacaoId = doacao.getId();
            if (doacaoId == null || doacaoId <= 0) {
                throw new IllegalStateException("Doacao inserida sem ID.");
            }
            System.out.println("APROVADO: POST doacao temporaria ID " + doacaoId);

            Doacao doacaoSalva = doacoes.buscarPorId(doacaoId);
            if (doacaoSalva == null || doacaoSalva.getDoador() == null
                    || doacaoSalva.getInstituicao() == null
                    || !doadorReferencia.getId().equals(doacaoSalva.getDoador().getId())
                    || !instituicaoId.equals(doacaoSalva.getInstituicao().getId())
                    || !LocalDate.now().equals(doacaoSalva.getDataDoacao())) {
                throw new IllegalStateException("GET da doacao nao preservou relacionamentos/data.");
            }
            System.out.println("APROVADO: GET doacao com JOIN e LocalDate recebidos da API");

            if (!instituicaoDao.possuiDoacoes(instituicaoId)) {
                throw new IllegalStateException("API nao identificou doacao vinculada.");
            }
            try {
                instituicoes.deletar(instituicaoId);
                throw new IllegalStateException("Instituicao com doacao foi excluida indevidamente.");
            } catch (IllegalStateException esperado) {
                // Confirma que a regra realmente impediu a exclusao.
                if (!instituicaoDao.possuiDoacoes(instituicaoId)) {
                    throw new IllegalStateException(
                            "A instituicao perdeu o vinculo durante o teste de integridade.", esperado);
                }
            }
            System.out.println("APROVADO: integridade impede excluir instituicao vinculada");

            doacaoSalva.setDescricao("Doacao temporaria atualizada por HTTP");
            doacoes.alterar(doacaoSalva);
            Doacao alterada = doacoes.buscarPorId(doacaoId);
            if (alterada == null
                    || !"Doacao temporaria atualizada por HTTP".equals(alterada.getDescricao())) {
                throw new IllegalStateException("PUT da doacao nao persistiu.");
            }
            System.out.println("APROVADO: PUT doacao e GET confirmacao");

            doacoes.deletar(doacaoId);
            if (doacoes.buscarPorId(doacaoId) != null) {
                throw new IllegalStateException("Doacao permaneceu apos DELETE.");
            }
            doacaoId = null;
            System.out.println("APROVADO: DELETE doacao temporaria e GET 404");

            instituicoes.deletar(instituicaoId);
            if (instituicoes.buscarPorId(instituicaoId) != null) {
                throw new IllegalStateException("Instituicao permaneceu apos DELETE.");
            }
            instituicaoId = null;
            System.out.println("APROVADO: DELETE instituicao temporaria e GET 404");
            sucesso = true;
            System.out.println("TODOS OS TESTES DE INSTITUICOES E DOACOES HTTP PASSARAM.");
        } catch (RuntimeException ex) {
            System.err.println("FALHOU: " + ex.getMessage());
        } finally {
            // Nunca executa DELETE sobre o doador de referencia.
            if (doacaoId != null) {
                try {
                    doacoes.deletar(doacaoId);
                    System.out.println("LIMPEZA: doacao temporaria ID " + doacaoId + " removida.");
                } catch (RuntimeException ex) {
                    sucesso = false;
                    System.err.println("ATENCAO: limpeza da doacao ID " + doacaoId
                            + " falhou: " + ex.getMessage());
                }
            }
            if (instituicaoId != null) {
                try {
                    instituicoes.deletar(instituicaoId);
                    System.out.println("LIMPEZA: instituicao temporaria ID " + instituicaoId
                            + " removida.");
                } catch (RuntimeException ex) {
                    sucesso = false;
                    System.err.println("ATENCAO: limpeza da instituicao ID " + instituicaoId
                            + " falhou: " + ex.getMessage());
                }
            }
        }
        if (!sucesso) {
            System.exit(1);
        }
    }
}
