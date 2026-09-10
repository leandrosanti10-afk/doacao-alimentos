package br.com.mvc.service;

import br.com.mvc.dao.InstituicaoDAO;
import br.com.mvc.model.Instituicao;

import java.util.List;

public class InstituicaoService {

    private final InstituicaoDAO instituicaoDAO =
            new InstituicaoDAO();

    public List<Instituicao> listar() {
        return instituicaoDAO.listar();
    }

    public Instituicao buscarPorId(Long id) {
        return instituicaoDAO.buscarPorId(id);
    }

    public void inserir(Instituicao instituicao) {
        validar(instituicao);
        instituicaoDAO.inserir(instituicao);
    }

    public void alterar(Instituicao instituicao) {
        validar(instituicao);
        instituicaoDAO.alterar(instituicao);
    }

    public void deletar(Long id) {

        if (instituicaoDAO.possuiDoacoes(id)) {

            throw new IllegalStateException(
                    "Não é possível excluir esta instituição, pois ela possui doações cadastradas."
            );
        }

        instituicaoDAO.deletar(id);
    }

    private void validar(Instituicao instituicao) {

        if (instituicao == null) {
            throw new IllegalArgumentException(
                    "Instituição obrigatória."
            );
        }

        if (instituicao.getNome() == null
                || instituicao.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "Nome obrigatório."
            );
        }

        if (instituicao.getEndereco() == null
                || instituicao.getEndereco().isBlank()) {

            throw new IllegalArgumentException(
                    "Endereço obrigatório."
            );
        }
    }
}