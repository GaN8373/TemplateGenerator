package generator.util;

import freemarker.template.Configuration;
import generator.data.TypeMapper;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateUtil {
    public static Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);


    public static String replaceWithRegexGroups(Collection<TypeMapper> typeMappers, String regexPattern, String inputText, String replacementTemplate) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(inputText);

        if (matcher.find()) {
            String result = replacementTemplate;
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String groupValue = matcher.group(i);
                if (groupValue != null) {
                    for (TypeMapper typeMapper : typeMappers) {
                        if (typeMapper.getAction().getConvertor().match(typeMapper.getRule(), groupValue)) {
                            groupValue = typeMapper.getType();
                            break;
                        }
                    }

                    result = result.replace("$" + i, groupValue);
                }
            }
            return result;
        } else {
            return replacementTemplate;
        }
    }
}
