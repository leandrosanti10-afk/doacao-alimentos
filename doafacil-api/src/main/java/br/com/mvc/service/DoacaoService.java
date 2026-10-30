package br.com.mvc.service;

import br.com.mvc.dao.DoacaoDAO;
import br.com.mvc.model.Doacao;

import java.util.List;

public class DoacaoService {

    private final DoacaoDAO doacaoDAO =
            new DoacaoDAO();

    public List<Doacao> listar() {
        return doacaoDAO.listar();
    }

    public Doacao buscarPorId(Long id) {
        return doacaoDAO.buscarPorId(id);
    }

    public void inserir(Doacao doacao) {

        validar(doacao);

        doacaoDAO.inserir(doacao);
    }

    public void alterar(Doacao doacao) {

        validar(doacao);

        doacaoDAO.alterar(doacao);
    }

    public void deletar(Long id) {

        doacaoDAO.deletar(id);
    }

    private void validar(
            Doacao doacao) {

        if (doacao == null) {

            throw new IllegalArgumentException(
                    "Doação obrigatória."
            );
        }

        if (doacao.getDoador() == null
                || doacao.getDoador().getId() == null) {

            throw new IllegalArgumentException(
                    "Doador obrigatório."
            );
        }

        if (doacao.getInstituicao() == null
                || doacao.getInstituicao().getId() == null) {

            throw new IllegalArgumentException(
                    "Instituição obrigatória."
            );
        }

        if (doacao.getDescricao() == null
                || doacao.getDescricao().isBlank()) {

            throw new IllegalArgumentException(
                    "Descrição obrigatória."
            );
        }

        if (doacao.getDataDoacao() == null) {

            throw new IllegalArgumentException(
                    "Data da doação obrigatória."
            );
        }
    }
}