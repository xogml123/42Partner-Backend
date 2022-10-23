package com.seoul.openproject.partner.domain.model.article;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TimeOfEating {
    NONE("상관 없음"), BREAKFAST("아침 식사"), LUNCH("점심 식사"), DUNCH("점저"), DINNER("저녁 식사"), MIDNIGHT("야식");

    private final String value;
}
