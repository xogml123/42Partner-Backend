package com.Seoul.OpenProject.Partner.domain.model.activity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ActivityType {

    ETC("기타", 0),
    MATCH_COMPLETE("매칭 완료", 5),
    MATCH_CANEL("매칭 취소", -10),
    MATCH_ABSENT("매칭 ", -5);

    private final String value;
    private final Integer score;
}
