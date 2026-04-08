package com.gamerstore.product_service.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

public class TextNormalizer {

    public static String normalize(String text) {
        if (text == null) return "";

        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim();
    }

    public static List<String> normalizeToWords(String text) {
        return Arrays.asList(normalize(text).split("\\s+"));
    }
}
