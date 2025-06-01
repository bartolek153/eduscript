package org.eduscript.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static boolean verbose = true;
    private static boolean debugMode = false;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ANSI Colors
    private static final String RESET = "\u001B[0m";

    private static final String BOLD = "\u001B[1m";

    private static final String GREEN = "\u001B[32m";

    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";
    // Box Drawing
    private static final String TOP_LEFT = "╭";
    private static final String TOP_RIGHT = "╮";
    private static final String BOTTOM_LEFT = "╰";

    private static final String BOTTOM_RIGHT = "╯";
    private static final String HORIZONTAL = "─";
    private static final String VERTICAL = "│";
    private static final String MIDDLE_LEFT = "├";
    private static final String MIDDLE_RIGHT = "┤";

    public static void enableVerbose(boolean enabled) {
        verbose = enabled;
    }

    public static void enableDebug(boolean enabled) {
        debugMode = enabled;
    }

    public static void printHeader(String title) {
        int width = Math.max(50, title.length() + 10);
        String line = HORIZONTAL.repeat(width - 2);
        System.out.println();
        System.out.println(BOLD + CYAN + TOP_LEFT + line + TOP_RIGHT + RESET);
        System.out.println(BOLD + CYAN + VERTICAL + centerText(title, width - 2) + VERTICAL + RESET);
        System.out.println(BOLD + CYAN + BOTTOM_LEFT + line + BOTTOM_RIGHT + RESET);
        System.out.println();
    }

    public static void printPhase(String phase) {
        System.out.println(BOLD + BLUE + "\n🔄 " + phase + RESET);
        System.out.println(BLUE + "   " + HORIZONTAL.repeat(phase.length() + 2) + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(BOLD + GREEN + timeStamp() + " ✅ " + message + RESET);
    }

    public static void printInfo(String message) {
        if (verbose) {
            System.out.println(CYAN + timeStamp() + " ℹ️  " + message + RESET);
        }
    }

    public static void printWarning(String message) {
        System.out.println(BOLD + YELLOW + timeStamp() + " ⚠️  " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(BOLD + RED + timeStamp() + " ❌ " + message + RESET);
    }

    public static void printDebug(String message) {
        if (debugMode) {
            System.out.println(MAGENTA + timeStamp() + " 🐞 " + message + RESET);
        }
    }

    public static void printStep(String step) {
        System.out.println(MAGENTA + timeStamp() + " → " + step + RESET);
    }

    public static void printFileInfo(String action, String filename) {
        System.out.println(CYAN + "📄 " + action + ": " + BOLD + filename + RESET);
    }

    public static void printStats(String label, String value) {
        System.out.println(YELLOW + "📊 " + label + ": " + BOLD + value + RESET);
    }

    public static void printSeparator() {
        System.out.println(CYAN + HORIZONTAL.repeat(60) + RESET);
    }

    public static void printBox(String title, String content) {
        int maxWidth = Math.max(title.length(), content.length()) + 4;
        String line = HORIZONTAL.repeat(maxWidth - 2);
        System.out.println(BOLD + CYAN + TOP_LEFT + line + TOP_RIGHT + RESET);
        System.out.println(BOLD + CYAN + VERTICAL + centerText(title, maxWidth - 2) + VERTICAL + RESET);
        System.out.println(BOLD + CYAN + MIDDLE_LEFT + line + MIDDLE_RIGHT + RESET);
        System.out.println(
                CYAN + VERTICAL + " " + content + " ".repeat(maxWidth - content.length() - 3) + VERTICAL + RESET);
        System.out.println(BOLD + CYAN + BOTTOM_LEFT + line + BOTTOM_RIGHT + RESET);
    }

    public static void printCompilationResult(boolean success, long timeMs) {
        System.out.println();
        if (success) {
            printBox("COMPILATION SUCCESSFUL", String.format("Completed in %d ms", timeMs));
            System.out.println(BOLD + GREEN + "🎉 Your EduScript program compiled successfully!" + RESET);
        } else {
            printBox("COMPILATION FAILED", String.format("Failed after %d ms", timeMs));
            System.out.println(BOLD + RED + "💥 Compilation failed. Please fix the errors above." + RESET);
        }
        System.out.println();
    }

    private static String timeStamp() {
        return "[" + LocalDateTime.now().format(timeFormatter) + "]";
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width)
            return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
}
