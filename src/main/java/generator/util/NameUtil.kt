package generator.util

import java.util.*
import java.util.regex.Pattern

object NameUtil {

    val WordMatch: Pattern = Pattern.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+")

    /**
     * Camel Case: 首字母小写，后续每个单词首字母大写
     */
    @JvmStatic
    fun toCamelCase(input: String?): String? {
        if (input.isNullOrEmpty()) {
            return input
        }

        val sb = checkNotNull(toPascalCase(input))

        return if (sb.isNotEmpty()) {
            sb.substring(0, 1).lowercase(Locale.getDefault()) + sb.substring(1)
        } else {
            ""
        }
    }

    /**
     * Pascal Case: 每个单词的首字母都大写
     */
    @JvmStatic
    fun toPascalCase(input: String?): String? {
        if (input.isNullOrEmpty()) {
            return input
        }

        val matcher = WordMatch.matcher(input)
        val sb = StringBuilder()

        while (matcher.find()) {
            val match = matcher.group()
            if (match.isNotEmpty()) {
                sb.append(match.substring(0, 1).uppercase(Locale.getDefault()))
                if (match.length > 1) {
                    sb.append(match.substring(1).lowercase(Locale.getDefault()))
                }
            }
        }
        return sb.toString()
    }


    /**
     * Snake Case: 所有字母小写，单词之间用下划线连接
     */
    @JvmStatic
    fun toSnakeCase(input: String?): String? {
        if (input.isNullOrEmpty()) {
            return input
        }
        val matcher = WordMatch.matcher(input)
        val sb = StringBuilder()
        var firstMatch = true

        while (matcher.find()) {
            val match = matcher.group()
            if (match.isNotEmpty()) {
                if (!firstMatch) {
                    sb.append("_")
                }
                sb.append(match.lowercase(Locale.getDefault()))
                firstMatch = false
            }
        }

        return sb.toString()
    }

    /**
     * Kebab Case: 所有字母小写，单词之间用连字符连接
     */
    @JvmStatic
    fun toKebabCase(input: String?): String? {
        if (input == null || input.isEmpty()) {
            return input
        }
        val matcher = WordMatch.matcher(input)
        val sb = StringBuilder()
        var firstMatch = true

        while (matcher.find()) {
            val match = matcher.group()
            if (match.isNotEmpty()) {
                if (!firstMatch) {
                    sb.append("-")
                }
                sb.append(match.lowercase(Locale.getDefault()))
                firstMatch = false
            }
        }

        return sb.toString()
    }

    /**
     * Screaming Snake Case: 所有字母大写，单词之间用下划线连接
     */
    @JvmStatic
    fun toScreamingSnakeCase(input: String?): String? {
        if (input.isNullOrEmpty()) {
            return input
        }
        val matcher = WordMatch.matcher(input)
        val sb = StringBuilder()
        var firstMatch = true

        while (matcher.find()) {
            val match = matcher.group()
            if (match.isNotEmpty()) {
                if (!firstMatch) {
                    sb.append("_")
                }
                sb.append(match.uppercase())
                firstMatch = false
            }
        }

        return sb.toString()
    }
}
