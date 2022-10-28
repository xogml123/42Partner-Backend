package com.seoul.openproject.partner.domain.model.article;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TypeOfEating {
    KOREAN("한식"), JAPANESE("일식"), CHINESE("중식"),
    WESTERN("양식"), ASIAN("아시안"), EXOTIC("이국적인");

    private final String value;
}
