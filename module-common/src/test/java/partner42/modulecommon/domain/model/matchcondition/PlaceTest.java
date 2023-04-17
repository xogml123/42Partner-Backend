package partner42.modulecommon.domain.model.matchcondition;


import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import partner42.modulecommon.domain.model.match.ConditionCategory;

class PlaceTest {

    @Test
    void extractPlaceFromMatchCondition_whenMatchCondition_Then() {
        List<Place> places = Place.extractPlaceFromMatchCondition(
            List.of(
                MatchCondition.of(Place.GAEPO.name(), ConditionCategory.Place),
                MatchCondition.of(Place.SEOCHO.name(), ConditionCategory.Place),
                MatchCondition.of(Place.OUT_OF_CLUSTER.name(), ConditionCategory.TimeOfEating)));
        //then
        assertThat(places).containsOnly(Place.GAEPO, Place.SEOCHO);
    }
}