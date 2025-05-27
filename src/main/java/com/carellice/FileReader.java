package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class FileReader {

    public static List<String> leggiFileRighe(String nomeFileRisorsa) {
        List<String> righe = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(classLoader.getResourceAsStream(nomeFileRisorsa), StandardCharsets.UTF_16LE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                righe.add(line);
            }

        } catch (IOException | NullPointerException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }

        return righe;
    }

}

