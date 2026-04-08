package com.gamerstore.shipping_service.utils;

public class CepRange {

    private final int inicio;
    private final int fim;
    private final String uf;

    public CepRange(int inicio, int fim, String uf) {
        this.inicio = inicio;
        this.fim = fim;
        this.uf = uf;
    }

    public boolean contains(int cep) {
        return cep >= inicio && cep <= fim;
    }

    public String getUf() {
        return uf;
    }
}
