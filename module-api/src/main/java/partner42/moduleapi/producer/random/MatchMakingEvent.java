package partner42.moduleapi.producer.random;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import partner42.moduleapi.dto.random.RandomMatchDto;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MatchMakingEvent {

    private LocalDateTime now;

    private ContentCategory contentCategory;

    private List<Place> placeList;

    private List<WayOfEating> wayOfEatingList;

    private List<TypeOfStudy> typeOfStudyList;

}
