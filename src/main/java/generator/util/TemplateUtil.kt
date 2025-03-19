package generator.util

import com.intellij.openapi.util.text.StringUtil
import freemarker.template.Configuration
import generator.config.TemplateConfig
import generator.config.TemplateConfig.Companion.fromProperties
import generator.data.TypeMapper
import java.util.regex.Pattern

object TemplateUtil {
    const val SPLIT_TAG_REGEX: String = "#region config"
    const val SPLIT_TAG: String = "#endregion"
    @JvmField
    var cfg: Configuration = Configuration(Configuration.VERSION_2_3_32)


    fun replaceWithRegexGroups(
        typeMappers: Collection<TypeMapper>,
        regexPattern: String,
        inputText: String,
        replacementTemplate: String
    ): String {
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(inputText)

        if (matcher.find()) {
            var result = replacementTemplate
            for (i in 1..matcher.groupCount()) {
                var groupValue = matcher.group(i)
                if (groupValue != null) {
                    for (typeMapper in typeMappers) {
                        if (typeMapper.action.convertor.match(typeMapper.rule, groupValue!!)) {
                            groupValue = typeMapper.type
                            break
                        }
                    }

                    result = result.replace("$$i", groupValue!!)
                }
            }
            return result
        } else {
            return replacementTemplate
        }
    }


    @JvmStatic
    fun extractConfig(region: String, template: String): Pair<TemplateConfig?,String> {
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

        val sourceCode = template.substring(endIndex + SPLIT_TAG.length).trim()

        val matcher = template.substring(beginIndex, endIndex)
        val configStr = matcher.replace(region, "").trim()
        if (StringUtil.isEmpty(configStr)) {
            return Pair(null, sourceCode)
        }

        return Pair(fromProperties(configStr), sourceCode)
    }
}
