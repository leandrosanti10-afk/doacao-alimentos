package br.com.mvc.api;

/** Erro padronizado da comunicacao Web -> API REST. */
public final class ApiException extends RuntimeException {
    private final int statusHttp;

    /** statusHttp = 0 indica falha de comunicacao ou de resposta, sem status HTTP. */
    public ApiException(int statusHttp, String mensagem) {
        super(mensagem);
        this.statusHttp = statusHttp;
    }

    public ApiException(int statusHttp, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.statusHttp = statusHttp;
    }

    public int getStatusHttp() {
        return statusHttp;
    }
}
