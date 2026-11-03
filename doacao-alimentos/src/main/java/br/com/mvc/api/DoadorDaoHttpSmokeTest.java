package br.com.mvc.api;

import br.com.mvc.dao.DoadorDAO;
import br.com.mvc.model.Doador;
import br.com.mvc.service.DoadorService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Teste de integracao real do Web -> API, com um doador temporario.
 * Nao modifica doadores existentes. Apaga o registro criado no bloco finally.
 * Nao e Servlet e nao deve ser chamado por um navegador.
 */
public final class DoadorDaoHttpSmokeTest {
    private DoadorDaoHttpSmokeTest() { }

    public static void main(String[] args) {
        DoadorService service = new DoadorService();
        DoadorDAO dao = new DoadorDAO();
        Long temporarioId = null;
        boolean sucesso = false;
        try {
            List<Doador> lista = service.listar();
            if (lista == null) {
                throw new IllegalStateException("Listagem nula.");
            }
            System.out.println("APROVADO: listar doadores via HTTP + JWT");

            long aleatorio = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
            String sufixo = Long.toString(aleatorio);
            Doador novo = new Doador();
            novo.setNome("Doador Temporario Web HTTP");
            novo.setCpfCnpj("40" + sufixo); // 11 digitos, diferente a cada teste.
            novo.setEmail("web.http." + sufixo + "@example.com");
            novo.setTelefone("349" + sufixo.substring(0, 8)); // 11 digitos.
            novo.setCidade("Uberaba");

            temporarioId = service.inserir(novo);
            if (temporarioId == null || temporarioId <= 0) {
                throw new IllegalStateException("Cadastro sem ID.");
            }
            System.out.println("APROVADO: cadastrar doador temporario via POST");

            Doador salvo = service.buscarPorId(temporarioId);
            if (salvo == null || !novo.getEmail().equals(salvo.getEmail())) {
                throw new IllegalStateException("Doador cadastrado nao foi encontrado.");
            }
            System.out.println("APROVADO: buscar doador temporario via GET por ID");

            if (!dao.existeEmail(novo.getEmail(), null)
                    || dao.existeEmail(novo.getEmail(), temporarioId)) {
                throw new IllegalStateException("Verificacao remota de duplicidade falhou.");
            }
            System.out.println("APROVADO: verificar duplicidade sem consultar MySQL no Web");

            salvo.setCidade("Ribeirao Preto");
            service.alterar(salvo);
            Doador atualizado = service.buscarPorId(temporarioId);
            if (atualizado == null || !"Ribeirao Preto".equals(atualizado.getCidade())) {
                throw new IllegalStateException("Atualizacao nao persistiu na API.");
            }
            System.out.println("APROVADO: atualizar doador temporario via PUT");

            if (dao.possuiDoacoes(temporarioId)) {
                throw new IllegalStateException("Doador novo apareceu com doacoes vinculadas.");
            }
            System.out.println("APROVADO: consultar vinculacoes via API de Doacoes");

            service.deletar(temporarioId);
            Doador excluido = service.buscarPorId(temporarioId);
            if (excluido != null) {
                throw new IllegalStateException("Doador continua disponivel apos exclusao.");
            }
            temporarioId = null;
            System.out.println("APROVADO: excluir doador temporario via DELETE e confirmar GET 404");
            sucesso = true;
            System.out.println("TODOS OS TESTES DO DOADORDAO HTTP PASSARAM.");
        } catch (RuntimeException ex) {
            System.err.println("FALHOU: " + ex.getMessage());
        } finally {
            if (temporarioId != null) {
                try {
                    service.deletar(temporarioId);
                    System.out.println("LIMPEZA: doador temporario excluido.");
                } catch (RuntimeException ex) {
                    System.err.println("ATENCAO: nao foi possivel excluir o doador temporario ID "
                            + temporarioId + ": " + ex.getMessage());
                    sucesso = false;
                }
            }
        }
        if (!sucesso) {
            System.exit(1);
        }
    }
}
