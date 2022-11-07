package com.seoul.openproject.partner.domain.model.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

//20글자 제한
@RequiredArgsConstructor
public enum TypeOfStudy {

   INNER_CIRCLE("본 과정"), NOT_INNER_CIRCLE("비본 과정");

    private final String value;

    @JsonCreator
    public static TypeOfStudy from(String s) {
        String target = s.toUpperCase();
        return TypeOfStudy.valueOf(target);
    }
}
