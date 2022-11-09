package com.seoul.openproject.partner.domain.model.matchcondition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import java.util.List;
import java.util.stream.Collectors;
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

    public static List<WayOfEating> extractWayOfEatingFromMatchCondition(List<MatchCondition> matchConditions) {
        return matchConditions.stream()
            .filter(matchCondition -> {
                String[] strings = WayOfEating.class.getName().split(".");
                return matchCondition.getConditionCategory().name()
                    .equalsIgnoreCase(strings[strings.length - 1]);
            })
            .map(matchCondition -> WayOfEating.from(matchCondition.getValue()))
            .collect(Collectors.toList());
    }

    public static List<MatchCondition> wayOfEatingToMatchCondition(List<WayOfEating> wayOfEatings) {
        return wayOfEatings.stream()
            .map(wayOfEating -> MatchCondition.of(
                wayOfEating.name(),
                ConditionCategory.valueOf(wayOfEating.name())

            ))
            .collect(Collectors.toList());
    }
}
