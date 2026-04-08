package com.gamerstore.shipping_service.utils;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CepUtils {

    private final List<CepRange> RANGES = List.of(
        new CepRange(01000000, 19999999, "SP"),
        new CepRange(20000000, 28999999, "RJ"),
        new CepRange(30000000, 39999999, "MG"),
        new CepRange(40000000, 48999999, "BA"),
        new CepRange(49000000, 49999999, "SE"),
        new CepRange(50000000, 56999999, "PE"),
        new CepRange(57000000, 57999999, "AL"),
        new CepRange(58000000, 58999999, "PB"),
        new CepRange(59000000, 59999999, "RN"),
        new CepRange(60000000, 63999999, "CE"),
        new CepRange(64000000, 64999999, "PI"),
        new CepRange(65000000, 65999999, "MA"),
        new CepRange(66000000, 68899999, "PA"),
        new CepRange(68900000, 68999999, "AP"),
        new CepRange(69000000, 69299999, "AM"),
        new CepRange(69300000, 69399999, "RR"),
        new CepRange(69400000, 69899999, "AM"),
        new CepRange(69900000, 69999999, "AC"),
        new CepRange(70000000, 72799999, "DF"),
        new CepRange(72800000, 72999999, "GO"),
        new CepRange(73000000, 73699999, "DF"),
        new CepRange(73700000, 76799999, "GO"),
        new CepRange(76800000, 76999999, "RO"),
        new CepRange(77000000, 77999999, "TO"),
        new CepRange(78000000, 78899999, "MT"),
        new CepRange(79000000, 79999999, "MS"),
        new CepRange(80000000, 87999999, "PR"),
        new CepRange(88000000, 89999999, "SC"),
        new CepRange(90000000, 99999999, "RS")
    );

    public String obterUfPorCep(String cep) {
        int cepNumerico = Integer.parseInt(cep.replaceAll("\\D", ""));

        return RANGES.stream()
            .filter(range -> range.contains(cepNumerico))
            .map(CepRange::getUf)
            .findFirst()
            .orElse("OUTROS");
    }
}
