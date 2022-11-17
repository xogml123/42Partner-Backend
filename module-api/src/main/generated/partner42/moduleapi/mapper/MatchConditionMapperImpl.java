package partner42.moduleapi.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-18T03:45:57+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.13 (Azul Systems, Inc.)"
)
@Component
public class MatchConditionMapperImpl implements MatchConditionMapper {

    @Override
    public MatchConditionDto entityToMatchConditionDto(MatchCondition article) {
        if ( article == null ) {
            return null;
        }

        MatchConditionDto.MatchConditionDtoBuilder matchConditionDto = MatchConditionDto.builder();

        return matchConditionDto.build();
    }
}
