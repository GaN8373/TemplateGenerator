package generator.util

import generator.config.TemplateConfig
import org.assertj.core.util.Strings
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.ValueSources

internal class TemplateUtilTest {


    @ParameterizedTest
    @ValueSource(strings = ["nonexistent region", "", "#region non", "#endregion"])
    fun extractConfig_RegionNotFound_ReturnsNullConfig(template: String) {
        val result = TemplateUtil.extractConfig("#region non", template)
        assertNull(result.first)
        assertEquals(template, result.second)
    }

    @ParameterizedTest
    @ValueSource(strings = ["#region no_end_tag", "#region no_end_tag\nfileName=a\ndir=b"])
    fun extractConfig_NoEndTag_ReturnsNullConfig(template: String) {
        val result = TemplateUtil.extractConfig("#region no_end_tag", template)
        assertNull(result.first)
        assertEquals(template, result.second)
    }

    @ParameterizedTest
    @ValueSource(strings = ["#region empty config\n#endregion", "#region empty config#endregion\n"])
    fun extractConfig_EmptyConfigString_ReturnsNullConfig(template: String) {

        val result = TemplateUtil.extractConfig("#region empty config", template)
        assertNull(result.first)
        assertEquals("", result.second)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "#region begin fileName=value1\ndir=value2\n#endregion\n",
            "#region begin fileName=value1\ndir=value2#endregion\n",
        ]
    )
    fun extractConfig_SuccessfulExtraction_ReturnsConfigAndEmptyTemplate(
        template: String
    ) {
        val expectedConfig = TemplateConfig.fromProperties("fileName=value1\ndir=value2")
        val result = TemplateUtil.extractConfig("#region begin", template)
        assertNotNull(result.first)
        assertEquals(expectedConfig!!.dir, result.first!!.dir)
        assertEquals(expectedConfig.fileName, result.first!!.fileName)
        assertEquals("", result.second)
    }


    @ParameterizedTest
    @ValueSource(strings = [
        "#region begin fileName=value1\ndir=value2\n#endregion\nABCD\n",
    ])
    fun extractTemplate_Successful(template: String) {

        val expectedConfig = TemplateConfig.fromProperties("fileName=value1\ndir=value2")
        val result = TemplateUtil.extractConfig("#region begin", template)
        assertNotNull(result.second)
        assertEquals("ABCD", result.second)
    }


    @ParameterizedTest
    @ValueSource(strings = [
        "ABCD#region begin fileName=value1\ndir=value2\n#endregion\nABCD\n",
    ])
    fun extractTemplate_Successful_Before(template: String) {

        val expectedConfig = TemplateConfig.fromProperties("fileName=value1\ndir=value2")
        val result = TemplateUtil.extractConfig("#region begin", template)
        assertNotNull(result.second)
        assertEquals("ABCDABCD", result.second)
    }


}