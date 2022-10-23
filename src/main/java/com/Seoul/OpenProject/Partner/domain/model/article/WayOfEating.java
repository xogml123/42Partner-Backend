package com.Seoul.OpenProject.Partner.domain.model.article;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WayOfEating {
    NONE("상관 없음"), DELIVERY("배달"), EATOUT("외식"), TAKEOUT("포장");

    private final String value;
}
