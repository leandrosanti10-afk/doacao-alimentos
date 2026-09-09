package br.com.mvc.service;

import br.com.mvc.dao.DoadorDAO;
import br.com.mvc.model.Doador;

import java.util.List;

public class DoadorService {

    private final DoadorDAO doadorDAO;

    public DoadorService() {
        this.doadorDAO = new DoadorDAO();
    }

    public List<Doador> listar() {
        return doadorDAO.listar();
    }

    public Doador buscarPorId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Id obrigatório.");
        }

        return doadorDAO.buscarPorId(id);
    }

    public void inserir(Doador doador) {

        validar(doador);

        doadorDAO.inserir(doador);
    }

    public void alterar(Doador doador) {

        if (doador.getId() == null) {
            throw new IllegalArgumentException(
                    "Id obrigatório para alteração."
            );
        }

        validar(doador);

        doadorDAO.alterar(doador);
    }

    public void deletar(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Id obrigatório."
            );
        }

        doadorDAO.deletar(id);
    }

    private void validar(Doador doador) {

        if (doador == null) {
            throw new IllegalArgumentException(
                    "Doador não pode ser nulo."
            );
        }

        if (doador.getNome() == null ||
                doador.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "Nome obrigatório."
            );
        }

        if (doador.getEmail() == null ||
                doador.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Email obrigatório."
            );
        }
    }
}