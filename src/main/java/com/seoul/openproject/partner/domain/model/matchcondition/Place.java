package com.seoul.openproject.partner.domain.model.matchcondition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import java.util.List;
import java.util.stream.Collectors;
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

    public static List<Place> extractPlaceFromMatchCondition(List<MatchCondition> matchConditions) {
        return matchConditions.stream()
            .filter(matchCondition -> {
                //Place
                String[] strings = Place.class.getName().split(".");
                return matchCondition.getConditionCategory().name()
                    .equals(strings[strings.length - 1]);
            })
            .map(matchCondition -> Place.from(matchCondition.getValue()))
            .collect(Collectors.toList());
    }

    public static List<MatchCondition> placeToMatchCondition(List<Place> places) {
        return places.stream()
            .map(place -> MatchCondition.of(
                place.name(),
                ConditionCategory.valueOf(place.name())

            ))
            .collect(Collectors.toList());
    }

}
