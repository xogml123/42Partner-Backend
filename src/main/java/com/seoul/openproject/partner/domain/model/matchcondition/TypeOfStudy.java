package com.seoul.openproject.partner.domain.model.matchcondition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

//20글자 제한
@RequiredArgsConstructor
public enum TypeOfStudy {

   INNER_CIRCLE("본 과정"), NOT_INNER_CIRCLE("비본 과정");

    private final String value;

    @JsonCreator
    public static TypeOfStudy from(String s) {
        String target = s.toUpperCase();
        return TypeOfStudy.valueOf(target);
    }

    public static List<TypeOfStudy> extractTypeOfStudyFromMatchCondition(List<MatchCondition> matchConditions) {
        return matchConditions.stream()
            .filter(matchCondition -> {
                String[] strings = TypeOfStudy.class.getName().split(".");
                return matchCondition.getConditionCategory().name()
                    .equalsIgnoreCase(strings[strings.length - 1]);
            })
            .map(matchCondition -> TypeOfStudy.from(matchCondition.getValue()))
            .collect(Collectors.toList());
    }

    public static List<MatchCondition> typeOfStudyToMatchCondition(List<TypeOfStudy> typeOfStudys) {
        return typeOfStudys.stream()
            .map(typeOfStudy -> MatchCondition.of(
                typeOfStudy.name(),
                ConditionCategory.valueOf(typeOfStudy.name())

            ))
            .collect(Collectors.toList());
    }
}
