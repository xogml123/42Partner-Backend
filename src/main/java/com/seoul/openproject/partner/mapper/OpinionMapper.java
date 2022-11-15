package com.seoul.openproject.partner.mapper;

import com.seoul.openproject.partner.domain.model.opinion.Opinion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpinionMapper {

    @Mapping(target="opinionId", source = "apiId")
    Opinion.OpinionOnlyIdResponse entityToOpinionOnlyIdResponse(Opinion opinion);

    @Mapping(target="opinionId", source = "apiId")
    @Mapping(target="nickname", source = "memberAuthor.nickname")
    @Mapping(target="parentId", source = "parentApiId")
    @Mapping(target="createdAt", source = "createdAt")
    @Mapping(target="updatedAt", source = "updatedAt")
    Opinion.OpinionResponse entityToOpinionResponse(Opinion opinion);
}
