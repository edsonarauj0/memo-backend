package com.nosde.memo.domain.enums;

public enum SexoEnum {
    MASCULINO(0, "Masculino"),
    FEMININO(1, "Feminino"),
    OUTRO(2, "Outro"),
    NAO_INFORMADO(3, "Não informado");

    private final int id;
    private final String descricao;

    SexoEnum(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public static SexoEnum getById(int id) {
        for (SexoEnum sexo : SexoEnum.values()) {
            if (sexo.getId() == id) {
                return sexo;
            }
        }
        return null;
    }

    public static SexoEnum getByDescricao(String descricao) {
        for (SexoEnum sexo : SexoEnum.values()) {
            if (sexo.getDescricao().equals(descricao)) {
                return sexo;
            }
        }
        return null;
    }
}
