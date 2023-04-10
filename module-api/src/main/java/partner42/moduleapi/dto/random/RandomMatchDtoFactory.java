package partner42.moduleapi.dto.random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;

@Component
public class RandomMatchDtoFactory {

    public RandomMatchDto createRandomMatchDto(ContentCategory contentCategory,
        List<RandomMatch> randomMatches) {

        RandomMatchDto randomMatchDto;
        List<Place> placeList = new ArrayList<>(randomMatches.stream()
            .map(RandomMatch::getRandomMatchCondition)
            .map(RandomMatchCondition::getPlace)
            .collect(Collectors.toSet()));
        if (contentCategory == ContentCategory.MEAL) {
            randomMatchDto = MealRandomMatchDto.builder()
                .contentCategory(contentCategory)
                .placeList(placeList)
                .wayOfEatingList(new ArrayList<>(randomMatches.stream()
                    .map(RandomMatch::getRandomMatchCondition)
                    .map(RandomMatchCondition::getWayOfEating)
                    .collect(Collectors.toSet())))
                .build();
        } else if (contentCategory == ContentCategory.STUDY) {
            randomMatchDto = StudyRandomMatchDto.builder()
                .contentCategory(contentCategory)
                .placeList(placeList)
                .typeOfStudyList(new ArrayList<>(randomMatches.stream()
                    .map(RandomMatch::getRandomMatchCondition)
                    .map(RandomMatchCondition::getTypeOfStudy)
                    .collect(Collectors.toSet())))
                .build();
        } else{
            throw new IllegalArgumentException("contentCategory가 잘못되었습니다.");
        }
        return randomMatchDto;
    }
}
