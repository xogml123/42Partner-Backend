package partner42.modulecommon.domain.model.random;

import java.util.Comparator;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.member.Member;
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
//@Table(name = "STUDY_RANDOM_MATCH")
public class StudyRandomMatch extends RandomMatch {

    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false)
    private TypeOfStudy typeOfStudy = null;

    @Builder
    public StudyRandomMatch(ContentCategory contentCategory, Place place,
        Member member, TypeOfStudy typeOfStudy) {
        super(contentCategory, place, member);
        this.typeOfStudy = typeOfStudy;
    }

    /********************************* 비지니스 로직 *********************************/

    @Override
    public boolean isMatchConditionEquals(RandomMatch randomMatch) {
        if (randomMatch instanceof StudyRandomMatch) {
            StudyRandomMatch studyRandomMatch = (StudyRandomMatch) randomMatch;
            return super.isMatchConditionEquals(randomMatch)
                && this.typeOfStudy.equals(studyRandomMatch.typeOfStudy);
        }
        return false;
    }

    public static class MatchConditionComparator implements Comparator<StudyRandomMatch> {
        @Override
        public int compare(StudyRandomMatch o1, StudyRandomMatch o2) {
            if (o1.getPlace().ordinal() != o2.getPlace().ordinal()) {
                return o1.getPlace().ordinal() - o2.getPlace().ordinal();
            } else {
                if (o1.getTypeOfStudy().ordinal() != o2.getTypeOfStudy().ordinal()){
                    return o1.getTypeOfStudy().ordinal() - o2.getTypeOfStudy().ordinal();
                } else {
                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                }
            }
        }
    }
}
