package com.employee.workwave.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {

    public static String capitalizeStringFields(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return sentence;
        }

        return Arrays.stream(sentence.trim().split("\\s"))
                .map(word -> Character.toTitleCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

}
