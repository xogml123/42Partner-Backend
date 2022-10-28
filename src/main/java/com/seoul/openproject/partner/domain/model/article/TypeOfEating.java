package com.seoul.openproject.partner.domain.model.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TypeOfEating {
    KOREAN("한식"), JAPANESE("일식"), CHINESE("중식"),
    WESTERN("양식"), ASIAN("아시안"), EXOTIC("이국적인"), CONVENIENCE("편의점");

    private final String value;

    @JsonCreator
    public static TypeOfEating from(String s) {
        String target = s.toUpperCase();
        return TypeOfEating.valueOf(target);
    }
}
