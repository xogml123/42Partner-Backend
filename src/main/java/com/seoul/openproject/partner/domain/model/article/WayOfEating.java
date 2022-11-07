package com.seoul.openproject.partner.domain.model.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

//20글자 제한.
@RequiredArgsConstructor
public enum WayOfEating {
    DELIVERY("배달"), EATOUT("외식"), TAKEOUT("포장");

    private final String value;
    @JsonCreator
    public static WayOfEating from(String s) {
        String target = s.toUpperCase();
        return WayOfEating.valueOf(target);
    }
}
