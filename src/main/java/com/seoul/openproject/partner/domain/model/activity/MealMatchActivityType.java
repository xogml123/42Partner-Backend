package com.seoul.openproject.partner.domain.model.activity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MealMatchActivityType {

    COMPLETE("매칭 완료", 5),
    CANEL("매칭 취소", -10),
    ABSENT("매칭 불참", -5);
    //후기에 따른 점수 반영.

    private final String value;
    private final Integer score;
}
