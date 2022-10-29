package com.seoul.openproject.partner.service.article;

import com.seoul.openproject.partner.domain.ArticleMatchCondition;
import com.seoul.openproject.partner.domain.model.ArticleMember;
import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleDto;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleOnlyIdResponse;
import com.seoul.openproject.partner.domain.model.article.Place;
import com.seoul.openproject.partner.domain.model.article.TimeOfEating;
import com.seoul.openproject.partner.domain.model.article.TypeOfEating;
import com.seoul.openproject.partner.domain.model.article.WayOfEating;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.repository.article.ArticleRepository;
import com.seoul.openproject.partner.domain.repository.matchcondition.MatchConditionRepository;
import com.seoul.openproject.partner.domain.repository.member.MemberRepository;
import com.seoul.openproject.partner.repository.ArticleMatchConditionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final MatchConditionRepository matchConditionRepository;
    private final ArticleMatchConditionRepository articleMatchConditionRepository;

    @Transactional
    public ArticleOnlyIdResponse createArticle(Article.ArticleDto articleRequest) {
        String memberId = articleRequest.getMemberId();
        Member member = memberRepository.findByApiId(memberId)
            .orElseThrow(() -> new EntityNotFoundException(memberId + "에 해당하는 회원이 없습니다."));

        ArticleMember articleMemberAuthor = ArticleMember.of(member, true);
        List<ArticleMatchCondition> articleMatchConditionList = allMatchConditionToArticleMatchCondition(articleRequest);

        Article article = articleRepository.save(Article.of(articleRequest.getTitle(),
            articleRequest.getContent(),
            articleRequest.getAnonymity(),
            articleMemberAuthor,
            articleMatchConditionList));

        return new ArticleOnlyIdResponse(article.getApiId());
    }
    @Transactional
    public ArticleOnlyIdResponse deleteArticle(String articleId) {

        articleRepository.deleteByApiId(articleId);

        return new ArticleOnlyIdResponse(articleId);
    }

    @Transactional
    public ArticleOnlyIdResponse updateArticle(ArticleDto articleRequest, String articleId) {
        Article article = articleRepository.findByApiId(articleId)
            .orElseThrow(() -> new EntityNotFoundException(articleId + "에 해당하는 게시글이 없습니다."));
        List<ArticleMatchCondition> articleMatchConditions = allMatchConditionToArticleMatchCondition(
            articleRequest);
        article.getArticleMatchConditions().stream()
                .forEach(articleMatchCondition -> articleMatchConditionRepository.delete(articleMatchCondition));
        article.update(articleRequest.getTitle(), articleRequest.getContent(),
            articleRequest.getAnonymity(), articleMatchConditions);
        return new ArticleOnlyIdResponse(article.getApiId());
    }


    private List<String> allMatchConditionToStringList(Article.ArticleDto articleRequest) {
        List<String> matchConditionStrings = new ArrayList<>();
        List<Place> place = articleRequest.getPlace();
        if (place == null) {
            place = new ArrayList<>();
        }
        matchConditionStrings.addAll(place.stream()
                .map(p ->
                    p.name())
                    .collect(Collectors.toList()));
        List<TimeOfEating> timeOfEating = articleRequest.getTimeOfEating();
        if (timeOfEating == null) {
            timeOfEating = new ArrayList<>();
        }
        matchConditionStrings.addAll(timeOfEating.stream()
            .map(p ->
                p.name())
            .collect(Collectors.toList()));
        List<TypeOfEating> typeOfEating = articleRequest.getTypeOfEating();
        if (typeOfEating == null) {
            typeOfEating = new ArrayList<>();
        }
        matchConditionStrings.addAll(typeOfEating.stream()
            .map(p ->
                p.name())
            .collect(Collectors.toList()));
        List<WayOfEating> wayOfEating = articleRequest.getWayOfEating();
        if (wayOfEating == null) {
            wayOfEating = new ArrayList<>();
        }
        matchConditionStrings.addAll(wayOfEating.stream()
            .map(p ->
                p.name())
            .collect(Collectors.toList()));
        return matchConditionStrings;
    }
    private List<ArticleMatchCondition> allMatchConditionToArticleMatchCondition(ArticleDto articleRequest) {
        return allMatchConditionToStringList(articleRequest).stream()
            .map((matchConditionString) ->
                matchConditionRepository.findByValue(matchConditionString).orElseThrow(() ->
                    new EntityNotFoundException(matchConditionString + "에 해당하는 매칭 조건이 없습니다.")
                ))
            .map(ArticleMatchCondition::of)
            .collect(Collectors.toList());
    }


}
