package com.seoul.openproject.partner.service.article;

import com.seoul.openproject.partner.domain.ArticleMatchCondition;
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

        List<String> matchConditionStrings = new ArrayList<>();
        addAllMatchCondition(articleRequest, matchConditionStrings);


        List<ArticleMatchCondition> articleMatchConditionList = new ArrayList<>();

        for (String matchConditionString : matchConditionStrings) {
            MatchCondition matchCondition = matchConditionRepository.findByValue(
                    matchConditionString)
                .orElseThrow(() -> new EntityNotFoundException(
                    matchConditionString + "에 해당하는 매칭 조건이 없습니다."));
            ArticleMatchCondition ar = ArticleMatchCondition.of(matchCondition);
            articleMatchConditionList.add(ar);
        }

        Article article = articleRepository.save(Article.of(articleRequest.getTitle(),
            articleRequest.getContent(),
            articleRequest.getAnonymity(),
            member,
            articleMatchConditionList));

        articleMatchConditionList.forEach(articleMatchCondition -> {
            articleMatchConditionRepository.save(articleMatchCondition);
        });

        return new ArticleOnlyIdResponse(article.getApiId());
    }

    private void addAllMatchCondition(ArticleDto articleRequest, List<String> matchConditionStrings) {
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
    }
}
