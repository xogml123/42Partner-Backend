package com.seoul.openproject.partner.domain.model.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

//20글자 이내 여야함.
@RequiredArgsConstructor
public enum Place {
    SEOCHO("서초 클러스터"), GAEPO("개포 클러스터"), OUT_OF_CLUSTER("클러스터 외부");

    private final String value;
    @JsonCreator
    public static Place from(String s) {
        String target = s.toUpperCase();
        return Place.valueOf(target);
    }

}
