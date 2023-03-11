//package partner42.modulecommon.domain.model.random;
//
//import java.util.Comparator;
//import partner42.modulecommon.domain.model.match.ContentCategory;
//import partner42.modulecommon.domain.model.matchcondition.Place;
//import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
//import partner42.modulecommon.domain.model.member.Member;
//import java.time.LocalDateTime;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.Table;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Entity
////@Table(name = "MEAL_RANDOM_MATCH")
//public class MealRandomMatch extends RandomMatch {
//
//
//
//
//    public MealRandomMatch(ContentCategory contentCategory, Place place,
//        Member member, WayOfEating wayOfEating) {
//        super(contentCategory, place, member);
//        this.wayOfEating = wayOfEating;
//    }
//
//    /********************************* 비지니스 로직 *********************************/
//    @Override
//    public boolean isMatchConditionEquals(RandomMatch randomMatch) {
//        if (randomMatch instanceof MealRandomMatch) {
//            MealRandomMatch mealRandomMatch = (MealRandomMatch) randomMatch;
//            return super.isMatchConditionEquals(randomMatch)
//                && this.wayOfEating.equals(mealRandomMatch.wayOfEating);
//        }
//        return false;
//    }
//
//    /********************************* Comparator *********************************/
//    public static class MatchConditionComparator implements Comparator<MealRandomMatch> {
//        @Override
//        public int compare(MealRandomMatch o1, MealRandomMatch o2) {
//            if (o1.getPlace().ordinal() != o2.getPlace().ordinal()) {
//                return o1.getPlace().ordinal() - o2.getPlace().ordinal();
//            } else {
//                if (o1.getWayOfEating().ordinal() != o2.getWayOfEating().ordinal()){
//                    return o1.getWayOfEating().ordinal() - o2.getWayOfEating().ordinal();
//                } else {
//                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
//                }
//            }
//        }
//    }
//}
