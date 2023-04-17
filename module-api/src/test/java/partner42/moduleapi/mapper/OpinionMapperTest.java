package partner42.moduleapi.mapper;


import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import partner42.moduleapi.dto.opinion.OpinionResponse;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.opinion.Opinion;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OpinionMapperImpl.class})
class OpinionMapperTest {
    @Autowired
    private OpinionMapper opinionMapper;

    @Test
    void entityToOpinionResponse() {
        Opinion parentOpinion = Opinion.of("content", Member.of("takim"),
            Article.of(null, null, null, null, null, null), null);
        Opinion opinion = Opinion.of("content", Member.of("takim"),
            Article.of(null, null, null, null, null, null), parentOpinion);

        //when
        OpinionResponse parentOpinionResponse = opinionMapper.entityToOpinionResponse(parentOpinion);
        OpinionResponse opinionResponse = opinionMapper.entityToOpinionResponse(opinion);
        assertThat(parentOpinionResponse)
            .usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo(OpinionResponse.builder()
                .content("content")
                .opinionId(parentOpinion.getApiId())
                .level(1)
                .createdAt(null)
                .updatedAt(null)
                .nickname("takim")
                .userId(null)
                .parentId(null)
                .build());
        assertThat(opinionResponse)
            .usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo(OpinionResponse.builder()
                .content("content")
                .opinionId(opinion.getApiId())
                .level(2)
                .createdAt(null)
                .updatedAt(null)
                .nickname("takim")
                .userId(null)
                .parentId(parentOpinion.getApiId())
                .build());
    }
}