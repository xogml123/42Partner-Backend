package com.Seoul.OpenProject.Partner.domain.model.activity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Type {
    MEAL_MATCH("매칭"), STUDY_MATCH("공부 매칭"), ACTIVITY("활동");

    private final String value;
}
