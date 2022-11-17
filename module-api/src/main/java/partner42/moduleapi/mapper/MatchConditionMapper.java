package partner42.moduleapi.mapper;

import org.mapstruct.Mapper;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;

@Mapper(componentModel = "spring")
public interface MatchConditionMapper {

    MatchConditionDto entityToMatchConditionDto(MatchCondition article);

}