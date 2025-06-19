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
                rule = "^(a)(b|B)\$", // 正则分组捕获
                type = "Type$1$2" // 占位符替换
            )
        )
        val result = TemplateUtil.convertType("aB", typeMappingUnits)
        assertEquals("TypeaB", result) // 输入转换为"ab"后匹配分组
    }

    /**
     * TC6: Regex替换逻辑（多分组替换）
     * 输入满足正则条件且type包含多个$1、$2等占位符
     */
    @Test
    fun `convertType should return replaced type with multiple groups when regex matches`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "^(a)(B)(c)\$", // 三个分组
                type = "Type$1$2$3" // 占位符替换
            )
        )
        val result = TemplateUtil.convertType("aBc", typeMappingUnits)
        assertEquals("TypeaBc", result) // 输入转换为"abc"后匹配三个分组
    }

      /**
     * TC6: Regex替换逻辑（多分组替换）
     * 输入满足正则条件且type包含多个$1、$2等占位符
     */
    @Test
    fun convertTypeReplace() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "^(a)(B)(c)\$", // 三个分组
                type = "Type$1$2$3" // 占位符替换
            ),
            TypeMappingUnit(
                action = MapperAction.Eq,
                rule = "a", // 再次替换
                type = "Hello"
            )
        )
        val result = TemplateUtil.convertType("aBc", typeMappingUnits)
        assertEquals("TypeHelloBc", result) // 输入转换为"abc"后匹配三个分组
    }

      /**
     * TC6: Regex替换逻辑（多分组替换）
     * 输入满足正则条件且type包含多个$1、$2等占位符
     */
    @Test
    fun convertTypeReplace2() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "^(a(B))(c)\$", // 三个分组
                type = "Type$1$2$3" // 占位符替换
            ),
            TypeMappingUnit(
                action = MapperAction.StartsWith,
                rule = "a", // 再次替换
                type = "Hello"
            )
        )
        val result = TemplateUtil.convertType("aBc", typeMappingUnits)
        assertEquals("TypeHelloBc", result) // 输入转换为"abc"后匹配三个分组
    }


    /**
     * TC7: Regex替换逻辑（带数字分组）
     * 输入满足正则条件且type包含$1、$2并带有数字
     */
    @Test
    fun `convertType should handle numeric groups correctly when regex matches`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "^([0-9]+)([a-zA-Z]+)\$", // 数字和字母分组
                type = "$2_$1" // 交换顺序
            )
        )
        val result = TemplateUtil.convertType("123XYZ", typeMappingUnits)
        assertEquals("XYZ_123", result) // 输入匹配数字和字母分组
    }

    /**
     * TC8: Regex替换逻辑（特殊字符处理）
     * 输入包含特殊字符时，确保正则仍能正确捕获并替换
     */
    @Test
    fun `convertType should handle special characters in input string`() {
        val typeMappingUnits = listOf(
            TypeMappingUnit(
                action = MapperAction.Regex,
                rule = "^(\\W+)(\\w+)\$", // 匹配非单词字符和单词字符
                type = "Special$2"
            )
        )
        val result = TemplateUtil.convertType("!@#test", typeMappingUnits)
        assertEquals("Specialtest", result) // 输入中的!@#是非单词字符
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


