package com.carellice;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StringsMatcher {

    public static final double INDEX_LEVENSHTEIN = 0.6;
    public static final double INDEX_JARO_WINKLER = 0.90;
    public static final double INDEX_JACCARD = 0.40;
    public static final double INDEX_DICE = 0.65;
    public static final double INDEX_ALL_ALGORITHMS = 2.6;
    public static final List<String> CHARS_TO_REMOVE = Arrays.asList(".", "&", "-", "'", "!", ",", "(", ")", "*", "%", ":", "+");

    /**
     * Normalizza la stringa. Trim, ToLowerCase, Remove CHARS_TO_REMOVE
     * @param string Stringa da normalizzare
     * @return Stringa normalizzata
     */
    private static String normalizeString(String string){
        string = StringUtils.trim(string);
        string = StringUtils.toRootLowerCase(string);
        for (String charToRemove : CHARS_TO_REMOVE) {
            string = StringUtils.replace(string, charToRemove, "");
        }
        return string;
    }

    /**
     * Calcola la distanza di Levenshtein tra due stringhe.
     * @param s1 Prima stringa
     * @param s2 Seconda stringa
     * @return La distanza di Levenshtein
     */
    private static int levenshteinDistance(String s1, String s2) {
        s1 = normalizeString(s1);
        s2 = normalizeString(s2);

        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null ? 0 : (s1 == null ? s2.length() : s1.length());
        }

        int[] prev = new int[s2.length() + 1];
        int[] curr = new int[s2.length() + 1];

        for (int j = 0; j <= s2.length(); j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[s2.length()];
    }

    /**
     * Calcola la similarità di Levenshtein normalizzata tra due stringhe.
     * @param s1 Prima stringa
     * @param s2 Seconda stringa
     * @return Un valore tra 0 e 1, dove 1 indica stringhe identiche
     */
    public static boolean levenshtein(String s1, String s2) {
        double points = getLevenshteinPoints(s1, s2);
        return points >= INDEX_LEVENSHTEIN;
    }
    public static double getLevenshteinPoints(String s1, String s2) {
        s1 = normalizeString(s1);
        s2 = normalizeString(s2);

        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null ? 1.0 : 0.0;
        }
        if (s1.equals(s2)) return 1.0;
        if (s1.length() == 0 || s2.length() == 0) return 0.0;

        int distance = levenshteinDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        double points = 1.0 - ((double) distance / maxLength);
        return points;
    }

    /**
     * Calcola la similarità di Jaro tra due stringhe.
     * @param s1 Prima stringa
     * @param s2 Seconda stringa
     * @return Un valore tra 0 e 1, dove 1 indica stringhe identiche
     */
    private static double jaro(String s1, String s2) {
        s1 = normalizeString(s1);
        s2 = normalizeString(s2);

        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null ? 1.0 : 0.0;
        }
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        // Calcola la massima distanza per considerare due caratteri "matching"
        int matchDistance = Math.max(s1.length(), s2.length()) / 2 - 1;
        if (matchDistance < 0) matchDistance = 0;

        // Array per tracciare i caratteri già abbinati
        boolean[] s1Matches = new boolean[s1.length()];
        boolean[] s2Matches = new boolean[s2.length()];

        // Conta matches
        int matches = 0;
        for (int i = 0; i < s1.length(); i++) {
            int start = Math.max(0, i - matchDistance);
            int end = Math.min(i + matchDistance + 1, s2.length());

            for (int j = start; j < end; j++) {
                if (!s2Matches[j] && s1.charAt(i) == s2.charAt(j)) {
                    s1Matches[i] = true;
                    s2Matches[j] = true;
                    matches++;
                    break;
                }
            }
        }

        if (matches == 0) return 0.0;

        // Conta trasposizioni
        int transpositions = 0;
        int k = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1Matches[i]) {
                while (!s2Matches[k]) k++;
                if (s1.charAt(i) != s2.charAt(k)) transpositions++;
                k++;
            }
        }

        // Il numero di mezze trasposizioni
        transpositions /= 2;

        // Calcola similarità Jaro
        double m = matches;
        return (m / s1.length() + m / s2.length() + (m - transpositions) / m) / 3.0;
    }

    /**
     * Calcola la similarità di Jaro-Winkler tra due stringhe.
     * @param s1 Prima stringa
     * @param s2 Seconda stringa
     * @return Un valore tra 0 e 1, dove 1 indica stringhe identiche
     */
    public static boolean jaroWinkler(String s1, String s2) {
        double points = getJaroWinklerPoints(s1, s2);
        return points >= INDEX_JARO_WINKLER;
    }
    public static double getJaroWinklerPoints(String s1, String s2) {
        s1 = normalizeString(s1);
        s2 = normalizeString(s2);

        double p = 0.1;
        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null ? 1.0 : 0.0;
        }

        double jaroSim = jaro(s1, s2);

        // Calcola la lunghezza del prefisso comune (max 4)
        int prefixLength = 0;
        int maxPrefixLength = Math.min(Math.min(s1.length(), s2.length()), 4);
        for (int i = 0; i < maxPrefixLength; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }

        // Jaro-Winkler = Jaro + (prefixLength * p * (1 - Jaro))
        double points = jaroSim + (prefixLength * p * (1 - jaroSim));
        return points;
    }

    /**
     * Calcola la distanza di Jaccard tra due stringhe.
     * @param s1 Prima stringa
     * @param s2 Seconda stringa
     * @return Un valore tra 0 e 1, dove 0 indica stringhe identiche e 1 diverse
     */
    public static boolean jaccard(String s1, String s2) {
        double points = getJaccardPoints(s1, s2);
        return points <= INDEX_JACCARD;
    }
    public static double getJaccardPoints(String s1, String s2) {
        s1 = normalizeString(s1);
        s2 = normalizeString(s2);

        int n = 2;
        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null ? 0.0 : 1.0;
        }

        // Converti in set di n-grammi
        Set<String> s1Ngrams = new HashSet<>();
        Set<String> s2Ngrams = new HashSet<>();

        // Se le stringhe sono troppo corte per gli n-grammi, usa lunghezza 1
        int actualN = (s1.length() < n || s2.length() < n) ? 1 : n;

        // Genera n-grammi per s1
        for (int i = 0; i <= s1.length() - actualN; i++) {
            s1Ngrams.add(s1.substring(i, i + actualN));
        }

        // Genera n-grammi per s2
        for (int i = 0; i <= s2.length() - actualN; i++) {
            s2Ngrams.add(s2.substring(i, i + actualN));
        }

        if (s1Ngrams.isEmpty() && s2Ngrams.isEmpty()) return 0.0;
        if (s1Ngrams.isEmpty() || s2Ngrams.isEmpty()) return 1.0;

        // Calcola l'intersezione
        Set<String> intersection = new HashSet<>(s1Ngrams);
        intersection.retainAll(s2Ngrams);

        // Calcola l'unione
        Set<String> union = new HashSet<>(s1Ngrams);
        union.addAll(s2Ngrams);

        // Distanza Jaccard = 1 - |intersezione| / |unione|
        double points = 1.0 - ((double) intersection.size() / union.size());
        return points;
    }

    /**
     * Calcola la similarità Sørensen-Dice tra due stringhe.
     * @param s1 Prima stringa
     * @param s2 Seconda stringa
     * @return Un valore tra 0 e 1, dove 1 indica alta similarità
     */
    public static boolean dice(String s1, String s2) {
        double points = getDicePoints(s1, s2);
        return points >= INDEX_DICE;
    }
    public static double getDicePoints(String s1, String s2) {
        s1 = normalizeString(s1);
        s2 = normalizeString(s2);

        int n = 2;
        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null ? 1.0 : 0.0;
        }

        // Converti in set di n-grammi
        Set<String> s1Ngrams = new HashSet<>();
        Set<String> s2Ngrams = new HashSet<>();

        // Se le stringhe sono troppo corte per gli n-grammi, usa lunghezza 1
        int actualN = (s1.length() < n || s2.length() < n) ? 1 : n;

        // Genera n-grammi per s1
        for (int i = 0; i <= s1.length() - actualN; i++) {
            s1Ngrams.add(s1.substring(i, i + actualN));
        }

        // Genera n-grammi per s2
        for (int i = 0; i <= s2.length() - actualN; i++) {
            s2Ngrams.add(s2.substring(i, i + actualN));
        }

        if (s1Ngrams.isEmpty() && s2Ngrams.isEmpty()) return 1.0;
        if (s1Ngrams.isEmpty() || s2Ngrams.isEmpty()) return 0.0;

        // Calcola l'intersezione
        Set<String> intersection = new HashSet<>(s1Ngrams);
        intersection.retainAll(s2Ngrams);

        // Dice coefficient = 2 * |intersezione| / (|s1| + |s2|)
        double points = 2.0 * intersection.size() / (s1Ngrams.size() + s2Ngrams.size());
        return points;
    }

    public static void printUniqueNonLetterNonDigitCharacters(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            System.out.println("La lista è vuota o nulla.");
            return;
        }

        Set<Character> specialChars = new LinkedHashSet<>(); // mantiene l'ordine

        for (String str : strings) {
            if (StringUtils.isNotBlank(str)) {
                for (char c : str.toCharArray()) {
                    if (!Character.isLetter(c) && !Character.isDigit(c)) {
                        specialChars.add(c);
                    }
                }
            }
        }

        // Stampa i caratteri unici che non sono né lettere né numeri
        System.out.print("Caratteri speciali unici: ");
        for (char c : specialChars) {
            System.out.print(c);
        }
        System.out.println();
    }
}