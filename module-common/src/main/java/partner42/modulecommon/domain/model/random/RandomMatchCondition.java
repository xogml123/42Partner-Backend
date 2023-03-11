package partner42.modulecommon.domain.model.random;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;

/**
 * 객체 생성 시 상태들이 한번에 초기 화 되지 않고 특정 경우에 사용 되는 상태들이
 * 구분 되기 때문에 원래는 분리되어야 하는 객체이지만 Embeddable로 되어 있어서
 * DB로 부터 데이터가 Mapping될 때 문제가 있을 것 같아 분리 하지 않음.
 */
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
@EqualsAndHashCode
public class RandomMatchCondition implements Comparable<RandomMatchCondition> {

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ContentCategory contentCategory;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Place place;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false)
    private WayOfEating wayOfEating = WayOfEating.NONE;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false)
    private TypeOfStudy typeOfStudy = TypeOfStudy.NONE;

    public static RandomMatchCondition of(ContentCategory contentCategory, Place place, TypeOfStudy typeOfStudy) {
        return RandomMatchCondition.builder()
            .contentCategory(contentCategory)
            .place(place)
            .typeOfStudy(typeOfStudy)
            .build();
    }

    public static RandomMatchCondition of(ContentCategory contentCategory, Place place, WayOfEating wayOfEating) {
        return RandomMatchCondition.builder()
            .contentCategory(contentCategory)
            .place(place)
            .wayOfEating(wayOfEating)
            .build();
    }

    @Override
    public int compareTo(RandomMatchCondition rmc) {
        if (contentCategory != rmc.contentCategory) {
            return contentCategory.compareTo(rmc.contentCategory);
        } else if (place != rmc.place) {
            return place.compareTo(rmc.place);
        } else if (wayOfEating != rmc.wayOfEating) {
            return wayOfEating.compareTo(rmc.wayOfEating);
        } else if (typeOfStudy != rmc.typeOfStudy) {
            return typeOfStudy.compareTo(rmc.typeOfStudy);
        } else {
            return 0;
        }
    }
}
