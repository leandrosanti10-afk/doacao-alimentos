package br.com.mvc.service;

import br.com.mvc.dao.DoacaoDAO;
import br.com.mvc.dao.DoadorDAO;
import br.com.mvc.dao.InstituicaoDAO;
import br.com.mvc.model.Doacao;

import java.util.List;
import java.util.NoSuchElementException;

/** Regras de negocio das doacoes, independentes dos Controllers HTTP. */
public class DoacaoService {

    private final DoacaoDAO doacaoDAO = new DoacaoDAO();
    private final DoadorDAO doadorDAO = new DoadorDAO();
    private final InstituicaoDAO instituicaoDAO = new InstituicaoDAO();

    public List<Doacao> listar() {
        return doacaoDAO.listar();
    }

    public Doacao buscarPorId(Long id) {
        return doacaoDAO.buscarPorId(id);
    }

    /** Persiste e devolve o ID gerado no MySQL para uso no HTTP 201. */
    public Long inserir(Doacao doacao) {
        validar(doacao);
        return doacaoDAO.inserir(doacao);
    }

    public void alterar(Doacao doacao) {
        if (doacao == null || doacao.getId() == null || doacao.getId() <= 0) {
            throw new IllegalArgumentException("ID da doacao obrigatorio.");
        }
        validar(doacao);
        doacaoDAO.alterar(doacao);
    }

    public void deletar(Long id) {
        doacaoDAO.deletar(id);
    }

    /** Confere os campos e as referencias antes de chegar ao INSERT/UPDATE. */
    private void validar(Doacao doacao) {
        if (doacao == null) {
            throw new IllegalArgumentException("Doacao obrigatoria.");
        }
        if (doacao.getDoador() == null || doacao.getDoador().getId() == null
                || doacao.getDoador().getId() <= 0) {
            throw new IllegalArgumentException("ID do doador obrigatorio e positivo.");
        }
        if (doacao.getInstituicao() == null || doacao.getInstituicao().getId() == null
                || doacao.getInstituicao().getId() <= 0) {
            throw new IllegalArgumentException("ID da instituicao obrigatorio e positivo.");
        }
        if (doacao.getDescricao() == null || doacao.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descricao obrigatoria.");
        }
        if (doacao.getDataDoacao() == null) {
            throw new IllegalArgumentException("Data da doacao obrigatoria.");
        }

        // Evita uma excecao de chave estrangeira para IDs inexistentes.
        // O proprio MySQL continua sendo a garantia final da integridade referencial.
        if (doadorDAO.buscarPorId(doacao.getDoador().getId()) == null) {
            throw new NoSuchElementException("Doador informado nao encontrado.");
        }
        if (instituicaoDAO.buscarPorId(doacao.getInstituicao().getId()) == null) {
            throw new NoSuchElementException("Instituicao informada nao encontrada.");
        }
        doacao.setDescricao(doacao.getDescricao().trim());
    }
}
