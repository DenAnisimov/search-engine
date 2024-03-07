package searchengine.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetBuilder {
    public static String getSnippet(String content, String searchTerm) {
        final int CONTEXT_LENGTH = 3;

        StringBuilder result = new StringBuilder();

        String[] sentences = content.split("[.!?]");

        if (searchTerm.length() >= 5) {
            searchTerm = searchTerm.substring(0, searchTerm.length() - 2);
        }

        Pattern pattern = Pattern.compile("\\b(" + searchTerm + "[а-яА-Я]*)\\b", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher;

        for (String sentence : sentences) {
            matcher = pattern.matcher(sentence);
            if (matcher.find()) {
                String[] words = sentence.split("\\s+");

                int index = -1;
                for (int i = 0; i < words.length; i++) {
                    if (words[i].contains(searchTerm)) {
                        index = i;
                        break;
                    }
                }

                int start = Math.max(0, index - CONTEXT_LENGTH);
                int end = Math.min(words.length, index + CONTEXT_LENGTH + 1);
                if (start > 0) {
                    result.append("... ");
                }
                for (int i = start; i < end; i++) {
                    if (i == index) {
                        result.append("<b>").append(words[i]).append("</b>").append(" ");
                    } else {
                        result.append(words[i]).append(" ");
                    }
                }
                if (end < words.length) {
                    result.append("...");
                }
                result.append(" ");

                matcher.reset();
            }
        }

        return result.toString().trim();
    }
}
