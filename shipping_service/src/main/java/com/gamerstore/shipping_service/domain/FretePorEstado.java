package com.gamerstore.shipping_service.domain;

import java.math.BigDecimal;
import java.util.Map;

public class FretePorEstado {

    public static final Map<String, BigDecimal> PAC = Map.ofEntries(
        Map.entry("SP", BigDecimal.valueOf(19.90)),
        Map.entry("RJ", BigDecimal.valueOf(24.90)),
        Map.entry("MG", BigDecimal.valueOf(22.90)),
        Map.entry("ES", BigDecimal.valueOf(23.90)),
        Map.entry("PR", BigDecimal.valueOf(24.90)),
        Map.entry("SC", BigDecimal.valueOf(26.90)),
        Map.entry("RS", BigDecimal.valueOf(27.90)),
        Map.entry("DF", BigDecimal.valueOf(25.90)),
        Map.entry("GO", BigDecimal.valueOf(25.90)),
        Map.entry("MS", BigDecimal.valueOf(29.90)),
        Map.entry("MT", BigDecimal.valueOf(32.90)),
        Map.entry("BA", BigDecimal.valueOf(29.90)),
        Map.entry("SE", BigDecimal.valueOf(30.90)),
        Map.entry("AL", BigDecimal.valueOf(30.90)),
        Map.entry("PE", BigDecimal.valueOf(29.90)),
        Map.entry("PB", BigDecimal.valueOf(31.90)),
        Map.entry("RN", BigDecimal.valueOf(31.90)),
        Map.entry("CE", BigDecimal.valueOf(32.90)),
        Map.entry("PI", BigDecimal.valueOf(33.90)),
        Map.entry("MA", BigDecimal.valueOf(34.90)),
        Map.entry("PA", BigDecimal.valueOf(36.90)),
        Map.entry("AP", BigDecimal.valueOf(39.90)),
        Map.entry("AM", BigDecimal.valueOf(39.90)),
        Map.entry("RR", BigDecimal.valueOf(42.90)),
        Map.entry("RO", BigDecimal.valueOf(38.90)),
        Map.entry("AC", BigDecimal.valueOf(44.90)),
        Map.entry("TO", BigDecimal.valueOf(34.90)),
        Map.entry("OUTROS", BigDecimal.valueOf(39.90))
    );

    public static final Map<String, BigDecimal> SEDEX = Map.ofEntries(
        Map.entry("SP", BigDecimal.valueOf(29.90)),
        Map.entry("RJ", BigDecimal.valueOf(39.90)),
        Map.entry("MG", BigDecimal.valueOf(34.90)),
        Map.entry("ES", BigDecimal.valueOf(35.90)),
        Map.entry("PR", BigDecimal.valueOf(36.90)),
        Map.entry("SC", BigDecimal.valueOf(38.90)),
        Map.entry("RS", BigDecimal.valueOf(39.90)),
        Map.entry("DF", BigDecimal.valueOf(37.90)),
        Map.entry("GO", BigDecimal.valueOf(37.90)),
        Map.entry("MS", BigDecimal.valueOf(44.90)),
        Map.entry("MT", BigDecimal.valueOf(49.90)),
        Map.entry("BA", BigDecimal.valueOf(44.90)),
        Map.entry("SE", BigDecimal.valueOf(45.90)),
        Map.entry("AL", BigDecimal.valueOf(45.90)),
        Map.entry("PE", BigDecimal.valueOf(44.90)),
        Map.entry("PB", BigDecimal.valueOf(46.90)),
        Map.entry("RN", BigDecimal.valueOf(46.90)),
        Map.entry("CE", BigDecimal.valueOf(47.90)),
        Map.entry("PI", BigDecimal.valueOf(48.90)),
        Map.entry("MA", BigDecimal.valueOf(49.90)),
        Map.entry("PA", BigDecimal.valueOf(52.90)),
        Map.entry("AP", BigDecimal.valueOf(59.90)),
        Map.entry("AM", BigDecimal.valueOf(59.90)),
        Map.entry("RR", BigDecimal.valueOf(64.90)),
        Map.entry("RO", BigDecimal.valueOf(54.90)),
        Map.entry("AC", BigDecimal.valueOf(69.90)),
        Map.entry("TO", BigDecimal.valueOf(49.90)),
        Map.entry("OUTROS", BigDecimal.valueOf(59.90))
    );
}
