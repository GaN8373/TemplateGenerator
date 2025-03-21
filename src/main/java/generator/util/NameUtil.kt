package generator.util;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtil {
    static Pattern WordMatch = Pattern.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+");

    /**
     * Camel Case: 首字母小写，后续每个单词首字母大写
     */
    public static @Nullable String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        var sb = toPascalCase(input);

        assert sb != null;
        if (!sb.isEmpty()) {
            return sb.substring(0, 1).toLowerCase() + sb.substring(1);
        } else {
            return "";
        }
    }

    /**
     * Pascal Case: 每个单词的首字母都大写
     */
    public static @Nullable String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        Matcher matcher = WordMatch.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String match = matcher.group();
            if (!match.isEmpty()) {
                sb.append(match.substring(0, 1).toUpperCase());
                if (match.length() > 1) {
                    sb.append(match.substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }


    /**
     * Snake Case: 所有字母小写，单词之间用下划线连接
     */
    public static @Nullable String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        Matcher matcher = WordMatch.matcher(input);
        StringBuilder sb = new StringBuilder();
        boolean firstMatch = true;

        while (matcher.find()) {
            String match = matcher.group();
            if (!match.isEmpty()) {
                if (!firstMatch) {
                    sb.append("_");
                }
                sb.append(match.toLowerCase());
                firstMatch = false;
            }
        }

        return sb.toString();
    }

    /**
     * Kebab Case: 所有字母小写，单词之间用连字符连接
     */
    public static @Nullable String toKebabCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        Matcher matcher = WordMatch.matcher(input);
        StringBuilder sb = new StringBuilder();
        boolean firstMatch = true;

        while (matcher.find()) {
            String match = matcher.group();
            if (!match.isEmpty()) {
                if (!firstMatch) {
                    sb.append("-");
                }
                sb.append(match.toLowerCase());
                firstMatch = false;
            }
        }

        return sb.toString();
    }

    /**
     * Screaming Snake Case: 所有字母大写，单词之间用下划线连接
     */
    public static @Nullable String toScreamingSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        Matcher matcher = WordMatch.matcher(input);
        StringBuilder sb = new StringBuilder();
        boolean firstMatch = true;

        while (matcher.find()) {
            String match = matcher.group();
            if (!match.isEmpty()) {
                if (!firstMatch) {
                    sb.append("_");
                }
                sb.append(match.toUpperCase(Locale.ROOT));
                firstMatch = false;
            }
        }

        return sb.toString();
    }

}
