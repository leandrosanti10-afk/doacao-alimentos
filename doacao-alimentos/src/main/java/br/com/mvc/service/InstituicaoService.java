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

    public Instituicao buscarPorId(
            Long id) {

        return instituicaoDAO
                .buscarPorId(id);
    }

    public void inserir(
            Instituicao instituicao) {

        validar(instituicao);

        if (instituicaoDAO.existeEmail(
                instituicao.getEmail(),
                null)) {

            throw new IllegalArgumentException(
                    "Email já cadastrado."
            );
        }

        if (instituicaoDAO.existeTelefone(
                instituicao.getTelefone(),
                null)) {

            throw new IllegalArgumentException(
                    "Telefone já cadastrado."
            );
        }

        instituicaoDAO.inserir(
                instituicao
        );
    }

    public void alterar(
            Instituicao instituicao) {

        validar(instituicao);

        if (instituicaoDAO.existeEmail(
                instituicao.getEmail(),
                instituicao.getId())) {

            throw new IllegalArgumentException(
                    "Email já cadastrado."
            );
        }

        if (instituicaoDAO.existeTelefone(
                instituicao.getTelefone(),
                instituicao.getId())) {

            throw new IllegalArgumentException(
                    "Telefone já cadastrado."
            );
        }

        instituicaoDAO.alterar(
                instituicao
        );
    }

    public void deletar(Long id) {

        if (instituicaoDAO
                .possuiDoacoes(id)) {

            throw new IllegalStateException(
                    "Não é possível excluir esta instituição, "
                    + "pois ela possui doações cadastradas."
            );
        }

        instituicaoDAO.deletar(id);
    }

    private void validar(
            Instituicao instituicao) {

        if (instituicao == null) {

            throw new IllegalArgumentException(
                    "Instituição obrigatória."
            );
        }

        if (instituicao.getNome() == null
                || instituicao
                .getNome()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "Nome obrigatório."
            );
        }

        if (instituicao.getEndereco() == null
                || instituicao
                .getEndereco()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "Endereço obrigatório."
            );
        }

        if (instituicao.getTelefone() == null
                || instituicao
                .getTelefone()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "Telefone obrigatório."
            );
        }

        if (instituicao.getEmail() == null
                || instituicao
                .getEmail()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "Email obrigatório."
            );
        }

        if (instituicao.getCidade() == null
                || instituicao
                .getCidade()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "Cidade obrigatória."
            );
        }
    }
}