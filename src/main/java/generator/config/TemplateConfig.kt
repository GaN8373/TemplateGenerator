package generator.config


class TemplateConfig {

    var fileName: String = ""
    var dir: String = ""

    companion object {
        @JvmStatic
        fun fromProperties(properties: String): TemplateConfig? {
            if (properties.isEmpty()) {
                return null
            }

            val templateConfig = TemplateConfig()
            properties.split("\n").forEach {
                val keyValue = it.split("=")
                if (keyValue.size >= 2) {
                    when (keyValue[0]) {
                        "fileName" -> templateConfig.fileName = keyValue[1].trim()
                        "dir" -> templateConfig.dir = keyValue[1].trim()
                    }
                }
            }

            return templateConfig
        }
    }
}