package generator.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NameUtilTest {
    @Test
    fun toPascalCase_NullInput_ReturnsNull() {
        val result = NameUtil.toPascalCase(null)
        assertEquals(null, result)
    }

    @Test
    fun toPascalCase_EmptyInput_ReturnsEmpty() {
        val result = NameUtil.toPascalCase("")
        assertEquals("", result)
    }

    @Test
    fun toPascalCase_SingleWordLowerCase_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("hello")
        assertEquals("Hello", result)
    }

    @Test
    fun toPascalCase_SingleWordUpperCase_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("HELLO")
        assertEquals("Hello", result)
    }

    @Test
    fun toPascalCase_SingleWordMixedCase_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("hELLo")
        assertEquals("Hello", result)
    }

    @Test
    fun toPascalCase_MultipleWordsLowerCase_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("hello world")
        assertEquals("HelloWorld", result)
    }

    @Test
    fun toPascalCase_MultipleWordsUpperCase_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("HELLO WORLD")
        assertEquals("HelloWorld", result)
    }

    @Test
    fun toPascalCase_MultipleWordsMixedCase_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("hELLo wORLd")
        assertEquals("HelloWorld", result)
    }

    @Test
    fun toPascalCase_MixedCaseAndNumbers_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("helloWORLD123")
        assertEquals("HelloWorld123", result)
    }

    @Test
    fun toPascalCase_SpecialCharacters_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("hello_world!")
        assertEquals("HelloWorld", result)
    }

    @Test
    fun toPascalCase_MultipleSpaces_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("hello   world")
        assertEquals("HelloWorld", result)
    }

    @Test
    fun toPascalCase_LeadingSpaces_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("   hello world")
        assertEquals("HelloWorld", result)
    }

    @Test
    fun toPascalCase_TrailingSpaces_ReturnsPascalCase() {
        val result = NameUtil.toPascalCase("hello world   ")
        assertEquals("HelloWorld", result)
    }
    @Test
    fun toSnakeCase_NullInput_ReturnsNull() {
        val result = NameUtil.toSnakeCase(null)
        assertEquals(null, result)
    }

    @Test
    fun toSnakeCase_EmptyInput_ReturnsEmpty() {
        val result = NameUtil.toSnakeCase("")
        assertEquals("", result)
    }

    @Test
    fun toSnakeCase_SingleWord_ReturnsSameWordInLowerCase() {
        val result = NameUtil.toSnakeCase("SingleWord")
        assertEquals("single_word", result)
    }

    @Test
    fun toSnakeCase_MultipleWords_ReturnsSnakeCase() {
        val result = NameUtil.toSnakeCase("MultipleWordsHere")
        assertEquals("multiple_words_here", result)
    }

    @Test
    fun toSnakeCase_MixedNumbersAndLetters_ReturnsSnakeCase() {
        val result = NameUtil.toSnakeCase("Mixed123Words")
        assertEquals("mixed123_words", result)
    }

    @Test
    fun toSnakeCase_MixedCase_ReturnsSnakeCase() {
        val result = NameUtil.toSnakeCase("MiXeDCase")
        assertEquals("mi_xe_d_case", result)
    }

    @Test
    fun toSnakeCase_SpecialCharacters_ReturnsSnakeCase() {
        val result = NameUtil.toSnakeCase("Special_Characters")
        assertEquals("special_characters", result)
    }

    @Test
    fun toCamelCase_NullInput_ReturnsNull() {
        val result = NameUtil.toCamelCase(null)
        Assertions.assertNull(result, "Expected null for null input")
    }

    @Test
    fun toCamelCase_EmptyInput_ReturnsEmptyString() {
        val result = NameUtil.toCamelCase("")
        Assertions.assertEquals("", result, "Expected empty string for empty input")
    }

    @Test
    fun toCamelCase_SingleWord_ReturnsLowercase() {
        val result = NameUtil.toCamelCase("SingleWord")
        Assertions.assertEquals("singleWord", result, "Expected single word to be converted to lowercase")
    }

    @Test
    fun toCamelCase_MultipleWords_ReturnsCamelCase() {
        val result = NameUtil.toCamelCase("MultipleWords")
        Assertions.assertEquals("multipleWords", result, "Expected multiple words to be converted to camelCase")
    }

    @Test
    fun toCamelCase_MixedNumbersAndLetters_ReturnsCamelCase() {
        val result = NameUtil.toCamelCase("Mixed123Words")
        Assertions.assertEquals(
            "mixed123Words",
            result,
            "Expected mixed numbers and letters to be converted to camelCase"
        )
    }

    @Test
    fun toCamelCase_PascalCase_ReturnsCamelCase() {
        val result = NameUtil.toCamelCase("PascalCase")
        Assertions.assertEquals("pascalCase", result, "Expected PascalCase to be converted to camelCase")
    }

    @Test
    fun toCamelCase_SnakeCase_ReturnsCamelCase() {
        val result = NameUtil.toCamelCase("snake_case")
        Assertions.assertEquals("snakeCase", result, "Expected snake_case to be converted to camelCase")
    }

    @Test
    fun toScreamingSnakeCase_NullInput_ReturnsNull() {
        val input: String? = null
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals(null, result)
    }

    @Test
    fun toScreamingSnakeCase_EmptyInput_ReturnsEmptyString() {
        val input = ""
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("", result)
    }

    @Test
    fun toScreamingSnakeCase_SingleUppercaseWord_ReturnsUppercase() {
        val input = "HTTP"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("HTTP", result)
    }

    @Test
    fun toScreamingSnakeCase_SingleLowercaseWord_ReturnsUppercase() {
        val input = "hello"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("HELLO", result)
    }

    @Test
    fun toScreamingSnakeCase_MixedCaseWord_ReturnsUppercase() {
        val input = "HelloWorld"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("HELLO_WORLD", result)
    }

    @Test
    fun toScreamingSnakeCase_Abbreviation_ReturnsUppercase() {
        val input = "HTTPServer"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("HTTP_SERVER", result)
    }

    @Test
    fun toScreamingSnakeCase_OnlyNumbers_ReturnsSame() {
        val input = "12345"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("12345", result)
    }

    @Test
    fun toScreamingSnakeCase_OnlyUppercaseLetters_ReturnsSame() {
        val input = "UPPER"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("UPPER", result)
    }

    @Test
    fun toScreamingSnakeCase_OnlyLowercaseLetters_ReturnsUppercase() {
        val input = "lower"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("LOWER", result)
    }

    @Test
    fun toScreamingSnakeCase_MixedCaseWithNumbers_ReturnsUppercase() {
        val input = "hello123world"
        val result = NameUtil.toScreamingSnakeCase(input)
        assertEquals("HELLO123_WORLD", result)
    }


    @Test
    fun toKebabCase_NullInput_ReturnsNull() {
        val input: String? = null
        val result = NameUtil.toKebabCase(input)
        assertEquals(null, result)
    }

    @Test
    fun toKebabCase_EmptyInput_ReturnsEmptyString() {
        val input = ""
        val result = NameUtil.toKebabCase(input)
        assertEquals("", result)
    }

    @Test
    fun toKebabCase_SingleWord_ReturnsLowercase() {
        val input = "SingleWord"
        val result = NameUtil.toKebabCase(input)
        assertEquals("single-word", result)
    }

    @Test
    fun toKebabCase_MultipleWords_ReturnsKebabCase() {
        val input = "MultipleWords"
        val result = NameUtil.toKebabCase(input)
        assertEquals("multiple-words", result)
    }

    @Test
    fun toKebabCase_MixedCase_ReturnsKebabCase() {
        val input = "MixedCASE"
        val result = NameUtil.toKebabCase(input)
        assertEquals("mixed-case", result)
    }

    @Test
    fun toKebabCase_WithNumbers_ReturnsKebabCase() {
        val input = "With123Numbers"
        val result = NameUtil.toKebabCase(input)
        assertEquals("with123-numbers", result)
    }

    @Test
    fun toKebabCase_WithSpecialCharacters_ReturnsKebabCase() {
        val input = "Special_Characters"
        val result = NameUtil.toKebabCase(input)
        assertEquals("special-characters", result)
    }
}