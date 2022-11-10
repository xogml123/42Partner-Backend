package com.seoul.openproject.partner.domain.model.match;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ContentCategory {
    MEAL, STUDY;

    @JsonCreator
    public static ContentCategory from(String s) {
        String target = s.toUpperCase();
        return ContentCategory.valueOf(target);
    }
}
