package br.com.mvc.util;

public class FormatadorCpfCnpj {

    public static String formatar(String cpfCnpj) {

        if (cpfCnpj == null) {
            return null;
        }

        String numeros =
                cpfCnpj.replaceAll("\\D", "");

        if (numeros.length() == 11) {

            return String.format(
                    "%s.%s.%s-%s",
                    numeros.substring(0, 3),
                    numeros.substring(3, 6),
                    numeros.substring(6, 9),
                    numeros.substring(9, 11)
            );
        }

        if (numeros.length() == 14) {

            return String.format(
                    "%s.%s.%s/%s-%s",
                    numeros.substring(0, 2),
                    numeros.substring(2, 5),
                    numeros.substring(5, 8),
                    numeros.substring(8, 12),
                    numeros.substring(12, 14)
            );
        }

        throw new IllegalArgumentException(
                "CPF/CNPJ deve possuir 11 ou 14 dígitos."
        );
    }
}