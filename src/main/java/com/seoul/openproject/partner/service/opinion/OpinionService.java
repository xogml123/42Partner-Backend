package com.seoul.openproject.partner.service.opinion;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.opnion.Opinion;
import com.seoul.openproject.partner.domain.model.opnion.Opinion.OpinionDto;
import com.seoul.openproject.partner.domain.model.opnion.Opinion.OpinionOnlyIdResponse;
import com.seoul.openproject.partner.domain.model.opnion.Opinion.OpinionResponse;
import com.seoul.openproject.partner.domain.model.opnion.Opinion.OpinionUpdateRequest;
import com.seoul.openproject.partner.dto.ListResponse;
import com.seoul.openproject.partner.mapper.OpinionMapper;
import com.seoul.openproject.partner.repository.OpinionRepository;
import com.seoul.openproject.partner.repository.article.ArticleRepository;
import com.seoul.openproject.partner.repository.member.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OpinionService {

    private final OpinionRepository opinionRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    private final OpinionMapper opinionMapper;

    @Transactional
    public OpinionOnlyIdResponse createOpinion(OpinionDto request, Long userId) {
        Article article = articleRepository.findByApiIdAndIsDeletedIsFalse(request.getArticleId())
            .orElseThrow(() -> new EntityNotFoundException(request.getArticleId() + " 게시글이 존재하지 않습니다."));
        Opinion opinion = Opinion.of(request.getContent(),
            memberRepository.findByUserId(userId),
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
            .orElseThrow(() -> new EntityNotFoundException(opinionId + "에 해당하는 댓글이 존재하지 않습니다."));
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
            .orElseThrow(() -> new EntityNotFoundException(opinionId + "에 해당하는 댓글이 존재하지 않습니다."));
        opinion.delete();
        return opinionMapper.entityToOpinionOnlyIdResponse(opinion);
    }

    @Transactional
    public OpinionOnlyIdResponse completeDeleteOpinion(String opinionId) {
        Opinion opinion = opinionRepository.findByApiId(opinionId)
            .orElseThrow(() -> new EntityNotFoundException(opinionId + "에 해당하는 댓글이 존재하지 않습니다."));
        opinionRepository.delete(opinion);
        return opinionMapper.entityToOpinionOnlyIdResponse(opinion);
    }
}
