package partner42.moduleapi.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.opinion.OpinionOnlyIdResponse;
import partner42.moduleapi.dto.opinion.OpinionResponse;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.opinion.Opinion;
import partner42.modulecommon.domain.model.user.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-04T21:37:11+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.13 (Azul Systems, Inc.)"
)
@Component
public class OpinionMapperImpl implements OpinionMapper {

    @Override
    public OpinionOnlyIdResponse entityToOpinionOnlyIdResponse(Opinion opinion) {
        if ( opinion == null ) {
            return null;
        }

        OpinionOnlyIdResponse.OpinionOnlyIdResponseBuilder opinionOnlyIdResponse = OpinionOnlyIdResponse.builder();

        opinionOnlyIdResponse.opinionId( opinion.getApiId() );

        return opinionOnlyIdResponse.build();
    }

    @Override
    public OpinionResponse entityToOpinionResponse(Opinion opinion) {
        if ( opinion == null ) {
            return null;
        }

        OpinionResponse.OpinionResponseBuilder opinionResponse = OpinionResponse.builder();

        opinionResponse.opinionId( opinion.getApiId() );
        opinionResponse.userId( opinionMemberAuthorUserApiId( opinion ) );
        opinionResponse.nickname( opinionMemberAuthorNickname( opinion ) );
        opinionResponse.parentId( opinionParentOpinionApiId( opinion ) );
        opinionResponse.createdAt( opinion.getCreatedAt() );
        opinionResponse.updatedAt( opinion.getUpdatedAt() );
        opinionResponse.content( opinion.getContent() );
        opinionResponse.level( opinion.getLevel() );

        return opinionResponse.build();
    }

    private String opinionMemberAuthorUserApiId(Opinion opinion) {
        if ( opinion == null ) {
            return null;
        }
        Member memberAuthor = opinion.getMemberAuthor();
        if ( memberAuthor == null ) {
            return null;
        }
        User user = memberAuthor.getUser();
        if ( user == null ) {
            return null;
        }
        String apiId = user.getApiId();
        if ( apiId == null ) {
            return null;
        }
        return apiId;
    }

    private String opinionMemberAuthorNickname(Opinion opinion) {
        if ( opinion == null ) {
            return null;
        }
        Member memberAuthor = opinion.getMemberAuthor();
        if ( memberAuthor == null ) {
            return null;
        }
        String nickname = memberAuthor.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private String opinionParentOpinionApiId(Opinion opinion) {
        if ( opinion == null ) {
            return null;
        }
        Opinion parentOpinion = opinion.getParentOpinion();
        if ( parentOpinion == null ) {
            return null;
        }
        String apiId = parentOpinion.getApiId();
        if ( apiId == null ) {
            return null;
        }
        return apiId;
    }
}
