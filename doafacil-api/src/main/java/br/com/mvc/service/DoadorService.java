package br.com.mvc.service;

import br.com.mvc.dao.DoadorDAO;
import br.com.mvc.model.Doador;

import java.util.List;
import br.com.mvc.util.FormatadorTelefone;
import br.com.mvc.util.FormatadorTexto;
import br.com.mvc.util.FormatadorCpfCnpj;

public class DoadorService {

    private final DoadorDAO doadorDAO =
            new DoadorDAO();

    public List<Doador> listar() {
        return doadorDAO.listar();
    }

    public Doador buscarPorId(Long id) {
        return doadorDAO.buscarPorId(id);
    }

    public Long inserir(Doador doador) {

        validar(doador);

        doador.setNome(
                FormatadorTexto.capitalizar(
                        doador.getNome()
                )
        );

        doador.setCidade(
                FormatadorTexto.capitalizar(
                        doador.getCidade()
                )
        );

        doador.setCpfCnpj(
                FormatadorCpfCnpj.formatar(
                        doador.getCpfCnpj()
                )
        );

        doador.setTelefone(
                FormatadorTelefone.formatar(
                        doador.getTelefone()
                )
        );

        if (doadorDAO.existeCpfCnpj(
                doador.getCpfCnpj(),
                null)) {

            throw new IllegalArgumentException(
                    "CPF/CNPJ já cadastrado."
            );
        }

        if (doadorDAO.existeEmail(
                doador.getEmail(),
                null)) {

            throw new IllegalArgumentException(
                    "Email já cadastrado."
            );
        }

        if (doadorDAO.existeTelefone(
                doador.getTelefone(),
                null)) {

            throw new IllegalArgumentException(
                    "Telefone já cadastrado."
            );
        }

        return doadorDAO.inserir(doador);
    }

    public void alterar(Doador doador) {

        validar(doador);

        doador.setNome(
                FormatadorTexto.capitalizar(
                        doador.getNome()
                )
        );

        doador.setCidade(
                FormatadorTexto.capitalizar(
                        doador.getCidade()
                )
        );

        doador.setCpfCnpj(
                FormatadorCpfCnpj.formatar(
                        doador.getCpfCnpj()
                )
        );

        doador.setTelefone(
                FormatadorTelefone.formatar(
                        doador.getTelefone()
                )
        );

        if (doadorDAO.existeCpfCnpj(
                doador.getCpfCnpj(),
                doador.getId())) {

            throw new IllegalArgumentException(
                    "CPF/CNPJ já cadastrado."
            );
        }

        if (doadorDAO.existeEmail(
                doador.getEmail(),
                doador.getId())) {

            throw new IllegalArgumentException(
                    "Email já cadastrado."
            );
        }

        if (doadorDAO.existeTelefone(
                doador.getTelefone(),
                doador.getId())) {

            throw new IllegalArgumentException(
                    "Telefone já cadastrado."
            );
        }

        doadorDAO.alterar(doador);
    }

    public void deletar(Long id) {

        if (doadorDAO.possuiDoacoes(id)) {

            throw new IllegalStateException(
                    "Não é possível excluir este doador, pois ele possui doações cadastradas."
            );
        }

        doadorDAO.deletar(id);
    }

    private void validar(Doador doador) {

        if (doador == null) {
            throw new IllegalArgumentException(
                    "Doador obrigatório."
            );
        }

        if (doador.getNome() == null
                || doador.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "Nome obrigatório."
            );
        }

        if (doador.getCpfCnpj() == null
                || doador.getCpfCnpj().isBlank()) {

            throw new IllegalArgumentException(
                    "CPF/CNPJ obrigatório."
            );
        }

        if (doador.getEmail() == null
                || doador.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Email obrigatório."
            );
        }

        if (doador.getTelefone() == null
                || doador.getTelefone().isBlank()) {

            throw new IllegalArgumentException(
                    "Telefone obrigatório."
            );
        }

        if (doador.getCidade() == null
                || doador.getCidade().isBlank()) {

            throw new IllegalArgumentException(
                    "Cidade obrigatória."
            );
        }
    }
}