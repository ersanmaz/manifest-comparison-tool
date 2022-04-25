package com.mct.comparator;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@Component
public class ManifestsComparator {

    public static final String NEW_LINE = "\n";
    public static final String SEMI_COLON = ": ";
    public static final String COMMA = ",";

    public String getDifference(File firstFile, File secondFile) {

        try (InputStream firstFileInputStream = new FileInputStream(firstFile);
             InputStream secondFileInputStream = new FileInputStream(secondFile)) {

            Attributes firstMainAttributes = new Manifest(firstFileInputStream).getMainAttributes();
            Attributes secondMainAttributes = new Manifest(secondFileInputStream).getMainAttributes();

            StringBuilder mainStringBuilder = new StringBuilder();
            firstMainAttributes.forEach((key, value) -> {
                if (!secondMainAttributes.containsKey(key)) {
                    mainStringBuilder.append(key).append(SEMI_COLON).append(value).append(NEW_LINE);
                } else {
                    List<String> firstFileValues = Arrays.stream(((String) value).split(COMMA)).collect(Collectors.toList());
                    List<String> secondFileValues = Arrays.stream(((String) secondMainAttributes.get(key)).split(COMMA)).collect(Collectors.toList());

                    StringBuilder sb = new StringBuilder();
                    firstFileValues.forEach(val -> {
                        if (!secondFileValues.contains(val)) {
                            sb.append(val).append(COMMA);
                        }
                    });
                    if (!"".equals(sb.toString())) {
                        int lastCharIndex = sb.length() - 1;
                        sb.deleteCharAt(lastCharIndex);
                        mainStringBuilder.append(key).append(SEMI_COLON).append(sb).append(NEW_LINE);
                    }
                }
            });
            return mainStringBuilder.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
