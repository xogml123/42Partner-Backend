package com.Seoul.OpenProject.Partner.domain.model.article;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PlaceOfEating {
    NONE("상관 없음"), SEOCHO("서초 클러스터"), GAEPO("개포 클러스터"), OUTOFCLUSTER("클러스터 외부");

    private final String value;


}
