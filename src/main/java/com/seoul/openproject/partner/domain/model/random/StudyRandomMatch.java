package com.seoul.openproject.partner.domain.model.random;

import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import com.seoul.openproject.partner.domain.model.matchcondition.Place;
import com.seoul.openproject.partner.domain.model.matchcondition.TypeOfStudy;
import com.seoul.openproject.partner.domain.model.member.Member;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "MEAL_RANDOM_MATCH")
public class StudyRandomMatch extends RandomMatch {

    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false)
    private TypeOfStudy typeOfStudy = null;

    @Builder
    public StudyRandomMatch(ContentCategory contentCategory, Place place,
        Member member, TypeOfStudy typeOfStudy, LocalDateTime createdAt) {
        super(contentCategory, place, createdAt, member);
        this.typeOfStudy = typeOfStudy;
    }

    @Override
    public String toStringKey() {
        return StringUtils.rightPad(place.name(), RandomMatch.STRING_CONDITION_MAX_LENGTH,
            RandomMatch.CONDITION_PAD_CHAR) +
            StringUtils.rightPad(typeOfStudy.name(), RandomMatch.STRING_CONDITION_MAX_LENGTH,
                RandomMatch.CONDITION_PAD_CHAR) +
            toValue();
    }

    @Override
    public String toKey() {
        return
            StringUtils.rightPad(contentCategory.name(),
                RandomMatch.STRING_CONDITION_MAX_LENGTH,
                RandomMatch.CONDITION_PAD_CHAR) +
                StringUtils.rightPad(place.name(), RandomMatch.STRING_CONDITION_MAX_LENGTH,
                    RandomMatch.CONDITION_PAD_CHAR) +
                StringUtils.rightPad(typeOfStudy.name(), RandomMatch.STRING_CONDITION_MAX_LENGTH,
                    RandomMatch.CONDITION_PAD_CHAR);
    }


    @Override
    public String toNumberKey() {
        return StringUtils.rightPad(Integer.toString(place.ordinal()),
            RandomMatch.INTEGER_CONDITION_MAX_LENGTH, RandomMatch.CONDITION_PAD_CHAR) +
            StringUtils.rightPad(Integer.toString(typeOfStudy.ordinal()),
                RandomMatch.INTEGER_CONDITION_MAX_LENGTH, RandomMatch.CONDITION_PAD_CHAR) +
            toValue();
    }

    @Override
    public String toAsciiKey() {
        return Character.toString(place.ordinal()) +
            Character.toString(typeOfStudy.ordinal()) +
            toValue();
    }
}
