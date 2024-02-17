package searchengine.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtil {
    private static final String REGEX_ORIGINAL_PATH = "^[https:]{0,}[\\/]{2}[a-z.]{1,}[a-z]{2,}";
    public static final String REGEX_PARTIAL_PATH = "^[\\/][a-zA-z \\/]{1,}$|^[\\/][a-zA-z \\/]{1,}\\.html$";

    public static boolean isOriginalPathFind(String path) {
        Pattern pattern = Pattern.compile(REGEX_ORIGINAL_PATH);
        Matcher matcher = pattern.matcher(path);

        return matcher.find();
    }

    public static boolean isPartialPathFind(String path) {
        Pattern pattern = Pattern.compile(REGEX_PARTIAL_PATH);
        Matcher matcher = pattern.matcher(path);

        return matcher.find();
    }

    public static String getOriginalPath(String path) {
        Pattern pattern = Pattern.compile(REGEX_ORIGINAL_PATH);
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return path.substring(matcher.start(), matcher.end());
        } else {
            return "";
        }
    }

    public static String getPartialPath(String path) {
        Pattern pattern = Pattern.compile(REGEX_ORIGINAL_PATH);
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return path.substring(matcher.end());
        } else {
            return "";
        }
    }
}
