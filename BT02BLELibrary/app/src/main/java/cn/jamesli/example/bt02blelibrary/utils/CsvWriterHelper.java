package cn.jamesli.example.bt02blelibrary.utils;

public class CsvWriterHelper {
    private static final String QUOTE = "\"";   // "content"

    public static String addStuff(final Integer text) {
        return QUOTE + text + QUOTE + ",";
    }

    public static String addStuff(final Long text) {
        return QUOTE + text + QUOTE + ",";
    }

    public static String addStuff(final boolean value) {
        return QUOTE + value + QUOTE + ",";
    }

    public static String addStuff(String text) {
        if (text == null) {     // treat null differently
            text = "<blank>";
        }
        text = text.replace(QUOTE, "'");    // downgrade " in the text to '

        return QUOTE + text.trim() + QUOTE + ",";
    }
}
