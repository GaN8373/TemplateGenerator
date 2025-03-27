package generator.util

import generator.MapperAction
import generator.config.TemplateConfig
import generator.data.TypeMappingUnit
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

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
    @ValueSource(
        strings = [
            "#region begin fileName=value1\ndir=value2\n#endregion\nABCD\n",
        ]
    )
    fun extractTemplate_Successful(template: String) {

        val expectedConfig = TemplateConfig.fromProperties("fileName=value1\ndir=value2")
        val result = TemplateUtil.extractConfig("#region begin", template)
        assertNotNull(result.second)
        assertEquals("ABCD", result.second)
    }


    @ParameterizedTest
    @ValueSource(
        strings = [
            "ABCD#region begin fileName=value1\ndir=value2\n#endregion\nABCD\n",
        ]
    )
    fun extractTemplate_Successful_Before(template: String) {

        val expectedConfig = TemplateConfig.fromProperties("fileName=value1\ndir=value2")
        val result = TemplateUtil.extractConfig("#region begin", template)
        assertNotNull(result.second)
        assertEquals("ABCDABCD", result.second)
    }


    /**
     * TC1: Regex替换逻辑（含分组替换）
     * 输入满足正则条件且type包含$1占位符
     */
    @Test
    fun `convertType should return replaced type when regex with groups matches`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "^(a)(b)\$", // 正则分组捕获
                type = "Type\$1\$2" // 占位符替换
            )
        )
        val result = TemplateUtil.convertType("aB", typeMappingUnits)
        assertEquals("Typeab", result) // 输入转换为"ab"后匹配分组
    }

    /**
     * TC2: 直接匹配类型（如Exact匹配）
     */
    @Test
    fun `convertType should return direct type when exact match succeeds`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "target",
                type = "ResultType"
            )
        )
        val result = TemplateUtil.convertType("TARGET", typeMappingUnits)
        assertEquals("ResultType", result) // 验证大小写不敏感
    }

    /**
     * TC3: 优先使用第一个匹配的Regex规则
     */
    @Test
    fun `convertType should use first matched regex mapper`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "a.*",
                type = "FirstMatch"
            ),
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "abc",
                type = "SecondMatch"
            )
        )
        val result = TemplateUtil.convertType("ABC", typeMappingUnits)
        assertEquals("FirstMatch", result)
    }

    /**
     * TC4: 正则失败但后续普通匹配成功
     */
    @Test
    fun `convertType should fallback to subsequent mappers when regex fails`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit( // 正则不匹配
                action = MapperAction.Regex,
                rule = "nomatch",
                type = "Wrong"
            ),
            TypeMappingUnit( // 普通匹配成功
                action = MapperAction.Contains,
                rule = "valid",
                type = "Correct"
            )
        )
        val result = TemplateUtil.convertType("VALID", typeMappingUnits)
        assertEquals("Correct", result)
    }

    /**
     * TC5: 无匹配返回null
     */
    @Test
    fun `convertType should return null when no mappers match`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "unknown",
                type = "Type"
            )
        )
        val result = TemplateUtil.convertType("test", typeMappingUnits)
        assertNull(result)
    }
}


