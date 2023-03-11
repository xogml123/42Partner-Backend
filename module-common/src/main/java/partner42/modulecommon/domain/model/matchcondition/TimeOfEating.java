package partner42.modulecommon.domain.model.matchcondition;

import com.fasterxml.jackson.annotation.JsonCreator;
import partner42.modulecommon.domain.model.match.ConditionCategory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum TimeOfEating {
    BREAKFAST("아침 식사"), LUNCH("점심 식사"), DUNCH("점저"), DINNER("저녁 식사"), MIDNIGHT("야식");

    private final String value;
    @JsonCreator
    public static TimeOfEating from(String s) {
        String target = s.toUpperCase();
        return TimeOfEating.valueOf(target);
    }

    public static List<TimeOfEating> extractTimeOfEatingFromMatchCondition(List<MatchCondition> matchConditions) {
        return matchConditions.stream()
            .filter(matchCondition -> {
                String[] strings = TimeOfEating.class.getName().split("\\.");
                return matchCondition.getConditionCategory().name()
                    .equalsIgnoreCase(strings[strings.length - 1]);
            })
            .map(matchCondition -> TimeOfEating.from(matchCondition.getValue()))
            .collect(Collectors.toList());
    }

//    public static List<MatchCondition> timeOfEatingToMatchCondition(List<TimeOfEating> timeOfEatings) {
//        return timeOfEatings.stream()
//            .map(timeOfEating -> MatchCondition.of(
//                timeOfEating.name(),
//                ConditionCategory.valueOf(timeOfEating.name())
//
//            ))
//            .collect(Collectors.toList());
//    }
}
