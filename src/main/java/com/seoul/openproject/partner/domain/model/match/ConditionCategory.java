package com.seoul.openproject.partner.domain.model.match;

import com.seoul.openproject.partner.domain.model.matchcondition.Place;

public enum ConditionCategory {
    Place,
    TimeOfEating, TypeOfEating, WayOfEating,
    TypeOfStudy;

//    public static ConditionCategory from(String s) {
//        return ConditionCategory.valueOf(s);
//    }
}
