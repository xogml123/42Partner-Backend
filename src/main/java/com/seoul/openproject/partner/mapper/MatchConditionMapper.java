package com.seoul.openproject.partner.mapper;

import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchConditionMapper {

    MatchCondition.MatchConditionDto entityToMatchConditionDto(MatchCondition article);

}