package com.seoul.openproject.partner.domain.model.matchcondition;

//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
//import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition;
//import java.util.List;
//import java.util.stream.Collectors;
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
//public enum TypeOfEating {
//    KOREAN("한식"), JAPANESE("일식"), CHINESE("중식"),
//    WESTERN("양식"), ASIAN("아시안"), EXOTIC("이국적인"), CONVENIENCE("편의점");
//
//    private final String value;
//
//    @JsonCreator
//    public static TypeOfEating from(String s) {
//        String target = s.toUpperCase();
//        return TypeOfEating.valueOf(target);
//    }
//
//    public static List<TypeOfEating> extractTypeOfEatingFromMatchCondition(List<MatchCondition> matchConditions) {
//        return matchConditions.stream()
//            .filter(matchCondition -> {
//                String[] strings = TypeOfEating.class.getName().split(".");
//                return matchCondition.getConditionCategory().name()
//                    .equalsIgnoreCase(strings[strings.length - 1]);
//            })
//            .map(matchCondition -> TypeOfEating.from(matchCondition.getValue()))
//            .collect(Collectors.toList());
//    }
//
//    public static List<MatchCondition> typeOfEatingToMatchCondition(List<TypeOfEating> typeOfEatings) {
//        return typeOfEatings.stream()
//            .map(typeOfEating -> MatchCondition.of(
//                typeOfEating.name(),
//                ConditionCategory.valueOf(typeOfEating.name())
//
//            ))
//            .collect(Collectors.toList());
//    }
//}
