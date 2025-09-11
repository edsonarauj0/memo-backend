package com.nosde.memo.infrastructure.helper;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ClassificacaoPerformance {
    private int ruimMax; 
    private int regularMax;
}
