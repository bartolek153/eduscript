package org.eduscript.logging;

import java.util.*;

import org.eduscript.model.LogEntry;

public class Logger {

    private static boolean verbose = true;
    private static boolean debugMode = false;

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

    public static String formatEntry(LogEntry entry) {
        String ts = "[" + entry.timeKey() + "]";
        String jobPrefix = (entry.getJobId() != null) ? "[Job " + entry.getJobId().substring(0, 8) + "] " : "";
        String message = jobPrefix + entry.getMessage();

        return switch (entry.getLevel()) {
            case "EXTERN" -> BLUE + "🔹 " + message + RESET;
            case "INFO" -> CYAN + ts + " ℹ️  " + message + RESET;
            case "ERROR" -> BOLD + RED + ts + " ❌ " + message + RESET;
            case "SUCCESS" -> BOLD + GREEN + ts + " ✅ " + message + RESET;
            case "WARNING" -> BOLD + YELLOW + ts + " ⚠️  " + message + RESET;
            case "DEBUG" -> MAGENTA + ts + " 🐞 " + message + RESET;
            case "STEP" -> MAGENTA + ts + " → " + message + RESET;
            case "STATS" -> YELLOW + "📊 " + message + RESET;
            case "FILE" -> CYAN + "📄 " + message + RESET;
            default -> message;
        };
    }

    private static final List<LogHandler> handlers = new ArrayList<>();

    public static void addHandler(LogHandler handler) {
        handlers.add(handler);
    }

    public static void clearHandlers() {
        for (LogHandler hdl : handlers)
            hdl.stop();
            
        handlers.clear();
    }

    public static void enableVerbose(boolean enabled) {
        verbose = enabled;
    }

    public static void enableDebug(boolean enabled) {
        debugMode = enabled;
    }

    // private static void record(String level, String message) {
    //     record(level, message, null);
    // }

    private static void record(String level, String message, String jobId) {
        LogEntry entry = new LogEntry(level, message, jobId);
        for (LogHandler handler : handlers) {
            handler.handle(entry);
        }
        // System.out.println(formatEntry(entry));
    }

    public static void printExtern(String message) {
        printExtern(message, null);
    }

    public static void printExtern(String message, String jobId) {
        record("EXTERN", message, jobId);
    }

    public static void printSuccess(String message) {
        printSuccess(message, null);
    }

    public static void printSuccess(String message, String jobId) {
        record("SUCCESS", message, jobId);
    }

    public static void printInfo(String message) {
        printInfo(message, null);
    }

    public static void printInfo(String message, String jobId) {
        if (verbose)
            record("INFO", message, jobId);
    }

    public static void printWarning(String message) {
        printWarning(message, null);
    }

    public static void printWarning(String message, String jobId) {
        record("WARNING", message, jobId);
    }

    public static void printError(String message) {
        printError(message, null);
    }

    public static void printError(String message, String jobId) {
        record("ERROR", message, jobId);
    }

    public static void printDebug(String message) {
        printDebug(message, null);
    }

    public static void printDebug(String message, String jobId) {
        if (debugMode)
            record("DEBUG", message, jobId);
    }

    public static void printStep(String step) {
        printStep(step, null);
    }

    public static void printStep(String step, String jobId) {
        if (verbose)
            record("STEP", step, jobId);
    }

    public static void printStats(String label, String value) {
        printStats(label, value, null);
    }

    public static void printStats(String label, String value, String jobId) {
        if (verbose)
            record("STATS", label + ": " + BOLD + value, jobId);
    }

    public static void printFileInfo(String action, String filename) {
        printFileInfo(action, filename, null);
    }

    public static void printFileInfo(String action, String filename, String jobId) {
        if (verbose)
            record("FILE", action + ": " + BOLD + filename, jobId);
    }

    public static void printSeparator() {
        if (verbose)
            System.out.println(CYAN + HORIZONTAL.repeat(60) + RESET);
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
        if (verbose) {
            System.out.println(BOLD + BLUE + "\n🔄 " + phase + RESET);
            System.out.println(BLUE + "   " + HORIZONTAL.repeat(phase.length() + 2) + RESET);
        }
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

    private static String centerText(String text, int width) {
        if (text.length() >= width)
            return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
}
