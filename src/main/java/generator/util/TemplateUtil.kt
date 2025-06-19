package generator.util

import com.intellij.openapi.util.text.StringUtil
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import generator.MapperAction
import generator.config.TemplateConfig
import generator.config.TemplateConfig.Companion.fromProperties
import generator.data.TypeMappingUnit
import java.io.Writer
import java.util.regex.Pattern


object TemplateUtil {
    const val SPLIT_TAG_REGEX: String = "#region config"
    const val SPLIT_TAG: String = "#endregion"

    @JvmField
    var cfg: Configuration = Configuration(Configuration.VERSION_2_3_32)


    @JvmStatic
    @Throws(TemplateException::class)
    fun evaluate(context: Map<String, Any>, writer: Writer, templateName: String, template: String): Boolean {
        val engine = Template(templateName, template, cfg)

        engine.process(context, writer)
        return true
    }


    private fun replaceWithRegexGroups(
        typeMappingUnits: Collection<TypeMappingUnit>,
        regexPattern: String,
        inputText: String,
        replacementTemplate: String
    ): String {
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(inputText)

        if (matcher.find()) {
            var result = replacementTemplate
            for (i in 1..matcher.groupCount()) {
                val groupValue = matcher.group(i)
                if (groupValue != null) {
                    val convertType = convertType(groupValue, typeMappingUnits)

                    result = result.replace("$$i", convertType ?: groupValue)
                }
            }
            return result
        } else {
            return replacementTemplate
        }
    }


    @JvmStatic
    fun extractConfig(region: String, template: String): Pair<TemplateConfig?, String> {
        val beginIndex = template.indexOf(region)
        if (beginIndex == -1) {
            return Pair(null, template)
        }

        var endIndex = template.substring(beginIndex + region.length).indexOf(SPLIT_TAG)
        while (endIndex < beginIndex) {
            if (endIndex == -1) {
                return Pair(null, template)
            }
            endIndex = template.substring(endIndex + SPLIT_TAG.length).indexOf(SPLIT_TAG)
        }
        endIndex += beginIndex + region.length

        val sourceCode = template.substring(0, beginIndex) + template.substring(endIndex + SPLIT_TAG.length).trim()

        val matcher = template.substring(beginIndex, endIndex)
        val configStr = matcher.replace(region, "").trim()
        if (StringUtil.isEmpty(configStr)) {
            return Pair(null, sourceCode)
        }

        return Pair(fromProperties(configStr), sourceCode)
    }


    @JvmStatic
    fun convertType(rawValue: String, typeMappingUnits: Collection<TypeMappingUnit>): String? {
        for (typeMapper in typeMappingUnits.sorted()) {
            val valueLowercase = rawValue.lowercase()
            var rawRule = typeMapper.rule

            if (typeMapper.action == MapperAction.Regex
                && typeMapper.type.contains("$")
                && typeMapper.action.match.match(rawRule, rawValue)
            ) {
                return replaceWithRegexGroups(
                    typeMappingUnits,
                    rawRule,
                    rawValue,
                    typeMapper.type
                )
            }

            if (typeMapper.action != MapperAction.Regex) {
                rawRule = rawRule.lowercase()
            }

            if (typeMapper.action.match.match(rawRule, valueLowercase)) {
                return typeMapper.type
            }
        }
        return null
    }
}
