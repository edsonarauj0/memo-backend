package com.nosde.memo.domain.enums;

public enum PeriodoRevisao {
    UM_DIA(0, "1 dia"),
    SETE_DIAS(1, "7 dias"),
    TRINTA_DIAS(2, "30 dias"),
    SESSENTA_DIAS(3, "60 dias"),
    CENTO_E_VINTE_DIAS(4, "120 dias");

    private final int valor;
    private final String descricao;

    PeriodoRevisao(int valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public int getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public static PeriodoRevisao fromValor(int valor) {
        for (PeriodoRevisao periodo : values()) {
            if (periodo.getValor() == valor) {
                return periodo;
            }
        }
        throw new IllegalArgumentException("Valor inválido para PeriodoRevisao: " + valor);
    }

    public static PeriodoRevisao fromDescricao(String descricao) {
        for (PeriodoRevisao periodo : values()) {
            if (periodo.getDescricao().equals(descricao)) {
                return periodo;
            }
        }
        throw new IllegalArgumentException("Descrição inválida para PeriodoRevisao: " + descricao);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
