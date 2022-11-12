package com.seoul.openproject.partner.config.enumconverter;

import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter<T> implements Converter<String, ContentCategory> {
    //query-parameter로 들어오는 경우
    @Override
    public ContentCategory convert(String source) {
        return ContentCategory.valueOf(source.toUpperCase());
    }
}