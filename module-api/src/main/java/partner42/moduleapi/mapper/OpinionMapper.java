package partner42.moduleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import partner42.moduleapi.dto.opinion.OpinionOnlyIdResponse;
import partner42.moduleapi.dto.opinion.OpinionResponse;
import partner42.modulecommon.domain.model.opinion.Opinion;

@Mapper(componentModel = "spring")
public interface OpinionMapper {

    @Mapping(target="opinionId", source = "apiId")
    OpinionOnlyIdResponse entityToOpinionOnlyIdResponse(Opinion opinion);

    @Mapping(target="opinionId", source = "apiId")
    @Mapping(target="userId", source = "memberAuthor.user.apiId")
    @Mapping(target="nickname", source = "memberAuthor.nickname")
    @Mapping(target="parentId", source = "parentApiId")
    @Mapping(target="createdAt", source = "createdAt")
    @Mapping(target="updatedAt", source = "updatedAt")
    OpinionResponse entityToOpinionResponse(Opinion opinion);
}
