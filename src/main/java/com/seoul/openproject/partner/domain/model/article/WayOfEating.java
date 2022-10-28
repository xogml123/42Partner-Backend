package com.seoul.openproject.partner.domain.model.article;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WayOfEating {
    DELIVERY("배달"), EATOUT("외식"), TAKEOUT("포장");

    private final String value;
}
