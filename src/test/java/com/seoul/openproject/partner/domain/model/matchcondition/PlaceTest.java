package com.seoul.openproject.partner.domain.model.matchcondition;


import com.seoul.openproject.partner.domain.model.match.ConditionCategory;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlaceTest {

    @Test
    void extractPlaceFromMatchCondition() {
//        Place.extractPlaceFromMatchCondition(
//            List.of(MatchCondition.of("SEOCHO", ConditionCategory.Place)));
        String[] split = Place.class.getName().split("\\.");
        for (String s : split) {
            System.out.println(s);

        }
    }
}