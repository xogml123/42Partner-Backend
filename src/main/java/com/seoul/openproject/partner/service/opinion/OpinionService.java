package com.seoul.openproject.partner.service.opinion;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.opinion.Opinion;
import com.seoul.openproject.partner.domain.model.opinion.Opinion.OpinionDto;
import com.seoul.openproject.partner.domain.model.opinion.Opinion.OpinionOnlyIdResponse;
import com.seoul.openproject.partner.domain.model.opinion.Opinion.OpinionResponse;
import com.seoul.openproject.partner.domain.model.opinion.Opinion.OpinionUpdateRequest;
import com.seoul.openproject.partner.dto.ListResponse;
import com.seoul.openproject.partner.error.exception.ErrorCode;
import com.seoul.openproject.partner.error.exception.NoEntityException;
import com.seoul.openproject.partner.mapper.OpinionMapper;
import com.seoul.openproject.partner.repository.opinion.OpinionRepository;
import com.seoul.openproject.partner.repository.article.ArticleRepository;
import com.seoul.openproject.partner.repository.member.MemberRepository;
import com.seoul.openproject.partner.repository.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OpinionService {

    private final OpinionRepository opinionRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    private final OpinionMapper opinionMapper;

    @Transactional
    public OpinionOnlyIdResponse createOpinion(OpinionDto request, String userId) {
        Article article = articleRepository.findByApiIdAndIsDeletedIsFalse(request.getArticleId())
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        Opinion opinion = Opinion.of(request.getContent(),
            userRepository.findByApiId(userId)
                .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
                .getMember(),
            article,
            request.getParentId(),
            request.getLevel()
        );
        opinionRepository.save(opinion);
        return opinionMapper.entityToOpinionOnlyIdResponse(opinion);
    }

    @Transactional
    public OpinionOnlyIdResponse updateOpinion(OpinionUpdateRequest request, String opinionId) {
        Opinion opinion = opinionRepository.findByApiId(opinionId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        opinion.updateContent(request.getContent());
        return opinionMapper.entityToOpinionOnlyIdResponse(opinion);
    }

    public ListResponse<OpinionResponse> findAllOpinionsInArticle(String articleId) {
        List<OpinionResponse> opinionResponses = opinionRepository.findAllByArticleApiIdAndIsDeletedIsFalse(articleId).stream()
            .map((o) ->
                opinionMapper.entityToOpinionResponse(o))
            .collect(Collectors.toList());

        return ListResponse.<OpinionResponse>builder()
            .values(opinionResponses)
            .valueCount(opinionResponses.size())
            .build();
    }

    @Transactional
    public OpinionOnlyIdResponse recoverableDeleteOpinion(String opinionId) {
        Opinion opinion = opinionRepository.findByApiId(opinionId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        opinion.delete();
        return opinionMapper.entityToOpinionOnlyIdResponse(opinion);
    }

    @Transactional
    public OpinionOnlyIdResponse completeDeleteOpinion(String opinionId) {
        Opinion opinion = opinionRepository.findByApiId(opinionId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        opinionRepository.delete(opinion);
        return opinionMapper.entityToOpinionOnlyIdResponse(opinion);
    }
}
