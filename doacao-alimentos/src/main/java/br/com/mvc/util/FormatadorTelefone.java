package br.com.mvc.util;

public class FormatadorTelefone {

    public static String formatar(String telefone) {

        if (telefone == null) {
            return null;
        }

        String numeros =
                telefone.replaceAll("\\D", "");

        if (numeros.length() == 11) {

            return String.format(
                    "(%s) %s-%s",
                    numeros.substring(0, 2),
                    numeros.substring(2, 7),
                    numeros.substring(7, 11)
            );
        }

        if (numeros.length() == 10) {

            return String.format(
                    "(%s) %s-%s",
                    numeros.substring(0, 2),
                    numeros.substring(2, 6),
                    numeros.substring(6, 10)
            );
        }

        throw new IllegalArgumentException(
                "Telefone deve possuir DDD e 10 ou 11 dígitos."
        );
    }
}