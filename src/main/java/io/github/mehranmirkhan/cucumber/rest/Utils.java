package io.github.mehranmirkhan.cucumber.rest;

public final class Utils {
    private Utils() {
    }

    public static Comparable parseIntOrDouble(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return Double.parseDouble(s);
        }
    }
}
