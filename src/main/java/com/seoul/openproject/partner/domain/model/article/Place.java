package com.seoul.openproject.partner.domain.model.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Place {
    SEOCHO("서초 클러스터"), GAEPO("개포 클러스터"), OUTOFCLUSTER("클러스터 외부");

    private final String value;
    @JsonCreator
    public static Place from(String s) {
        String target = s.toUpperCase();
        return Place.valueOf(target);
    }

}
