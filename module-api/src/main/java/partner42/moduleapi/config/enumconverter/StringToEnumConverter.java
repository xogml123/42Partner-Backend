package partner42.moduleapi.config.enumconverter;


import org.springframework.core.convert.converter.Converter;
import partner42.modulecommon.domain.model.match.ContentCategory;

public class StringToEnumConverter<T> implements Converter<String, ContentCategory> {
    //query-parameter로 들어오는 경우
    @Override
    public ContentCategory convert(String source) {
        return ContentCategory.valueOf(source.toUpperCase());
    }
}