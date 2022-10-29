package com.seoul.openproject.partner.domain.model.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TimeOfEating {
    BREAKFAST("아침 식사"), LUNCH("점심 식사"), DUNCH("점저"), DINNER("저녁 식사"), MIDNIGHT("야식");

    private final String value;
    @JsonCreator
    public static TimeOfEating from(String s) {
        String target = s.toUpperCase();
        return TimeOfEating.valueOf(target);
    }
}
