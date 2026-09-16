package br.com.mvc.util;

public class FormatadorTexto {

    public static String capitalizar(String texto) {

        if (texto == null) {
            return null;
        }

        texto = texto.trim().toLowerCase();

        String[] palavras = texto.split("\\s+");

        StringBuilder resultado = new StringBuilder();

        for (String palavra : palavras) {

            if (palavra.isBlank()) {
                continue;
            }

            String palavraFormatada =
                    palavra.substring(0, 1).toUpperCase()
                    + palavra.substring(1);

            if (resultado.length() > 0) {
                resultado.append(" ");
            }

            resultado.append(palavraFormatada);
        }

        return resultado.toString();
    }
}