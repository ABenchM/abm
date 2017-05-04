package de.fraunhofer.abm.projectanalyzer.license;

public class StringComparison {
    public static int percentageOfEquality(String s, String t) {
        // check if strings are empty
        if (s == null || t == null || s.length() == 0 || t.length() == 0) {
            return 0;
        }

        // check if the strings are equal
        if (s.equals(t)) {
            return 100;
        }

        // check if one string is a substring of the other
        String shorter;
        String longer;
        if (s.length() > t.length()) {
            shorter = t;
            longer = s;
        } else {
            shorter = s;
            longer = t;
        }
        if (longer.startsWith(shorter) && longer.length() > shorter.length()) {
            if (longer.charAt(shorter.length()) == ' ') {
                return 99;
            } else {
                return 98;
            }
        }

        s = s.toLowerCase();
        s = s.replaceAll("-", " ");
        s = s.replaceAll(":", " ");
        s = s.replaceAll(";", " ");
        s = s.replaceAll("\\|", " ");
        s = s.replaceAll("_", " ");
        s = s.replaceAll("\\.", "\\. ");
        s = s.trim();
        t = t.toLowerCase();
        t = t.replaceAll("-", " ");
        t = t.replaceAll(":", " ");
        t = t.replaceAll(";", " ");
        t = t.replaceAll("\\|", " ");
        t = t.replaceAll("_", " ");
        t = t.replaceAll("\\.", "\\. ");
        t = t.trim();

        // calculate levenshteinDistance
        int levenshteinDistance = getLevenshteinDistance(s, t);
        int length = Math.max(s.length(), t.length());

        // calculate the percentage of equality
        int percentage = 100 - (int) ((double) levenshteinDistance * 100 / length);
        return percentage;
    }

    public static int getLevenshteinDistance(String s, String t) {
        int n = s.length();
        int m = t.length();
        int d[][] = new int[n + 1][m + 1];
        int i;
        int j;
        int cost;

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) {
            for (j = 1; j <= m; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                d[i][j] = min(d[i - 1][j] + 1, // insertion
                        d[i][j - 1] + 1, // deletion
                        d[i - 1][j - 1] + cost); // substitution
            }
        }
        return d[n][m];
    }

    private static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
}
