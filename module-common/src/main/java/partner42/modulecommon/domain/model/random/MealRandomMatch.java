package partner42.modulecommon.domain.model.random;

import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "MEAL_RANDOM_MATCH")
public class MealRandomMatch extends RandomMatch{



    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private WayOfEating wayOfEating ;

    public MealRandomMatch(ContentCategory contentCategory, Place place,
        Member member, WayOfEating wayOfEating, LocalDateTime createdAt) {
        super(contentCategory, place, createdAt, member);
        this.wayOfEating = wayOfEating;
    }


    @Override
    public String toStringKey(){
        return StringUtils.rightPad(place.name(), RandomMatch.STRING_CONDITION_MAX_LENGTH,
            RandomMatch.CONDITION_PAD_CHAR) +
            StringUtils.rightPad(wayOfEating.name(), RandomMatch.STRING_CONDITION_MAX_LENGTH,
                RandomMatch.CONDITION_PAD_CHAR) +
            keyPrefix();
    }


    @Override
    public String toNumberKey(){
        return StringUtils.rightPad(Integer.toString(place.ordinal()),
            RandomMatch.INTEGER_CONDITION_MAX_LENGTH, RandomMatch.CONDITION_PAD_CHAR) +
            StringUtils.rightPad(Integer.toString(wayOfEating.ordinal()),
                RandomMatch.INTEGER_CONDITION_MAX_LENGTH, RandomMatch.CONDITION_PAD_CHAR) +
            keyPrefix();
    }
    @Override
    public String toAsciiKey(){
        return Character.toString(place.ordinal()) +
            Character.toString(wayOfEating.ordinal()) +
            keyPrefix();
    }
}
