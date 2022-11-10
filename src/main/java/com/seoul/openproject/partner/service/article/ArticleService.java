package com.seoul.openproject.partner.service.article;

import com.seoul.openproject.partner.domain.model.activity.Activity;
import com.seoul.openproject.partner.domain.model.activity.ActivityType;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition.MatchConditionDto;
import com.seoul.openproject.partner.domain.model.matchcondition.TypeOfStudy;
import com.seoul.openproject.partner.domain.model.match.Match;
import com.seoul.openproject.partner.domain.model.match.MatchMember;
import com.seoul.openproject.partner.domain.model.match.MatchStatus;
import com.seoul.openproject.partner.domain.model.match.MethodCategory;
import com.seoul.openproject.partner.domain.model.matchcondition.ArticleMatchCondition;
import com.seoul.openproject.partner.domain.model.article.ArticleMember;
import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleDto;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleOnlyIdResponse;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleReadOneResponse;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleReadResponse;
import com.seoul.openproject.partner.domain.model.matchcondition.Place;
import com.seoul.openproject.partner.domain.model.matchcondition.TimeOfEating;
import com.seoul.openproject.partner.domain.model.matchcondition.WayOfEating;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchConditionMatch;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.model.member.Member.MemberDto;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.error.exception.ErrorCode;
import com.seoul.openproject.partner.error.exception.NoEntityException;
import com.seoul.openproject.partner.error.exception.NotAuthorException;
import com.seoul.openproject.partner.error.exception.SlackException;
import com.seoul.openproject.partner.repository.activity.ActivityRepository;
import com.seoul.openproject.partner.repository.article.ArticleRepository;
import com.seoul.openproject.partner.repository.article.ArticleSearch;
import com.seoul.openproject.partner.repository.articlemember.ArticleMemberRepository;
import com.seoul.openproject.partner.repository.match.MatchMemberRepository;
import com.seoul.openproject.partner.repository.match.MatchRepository;
import com.seoul.openproject.partner.repository.matchcondition.MatchConditionMatchRepository;
import com.seoul.openproject.partner.repository.matchcondition.MatchConditionRepository;
import com.seoul.openproject.partner.repository.member.MemberRepository;
import com.seoul.openproject.partner.repository.matchcondition.ArticleMatchConditionRepository;
import com.seoul.openproject.partner.mapper.MatchConditionMapper;
import com.seoul.openproject.partner.mapper.MemberMapper;
import com.seoul.openproject.partner.repository.user.UserRepository;
import com.seoul.openproject.partner.service.slack.SlackBotService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleService {

    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final MatchConditionRepository matchConditionRepository;
    private final ArticleMatchConditionRepository articleMatchConditionRepository;
    private final ArticleMemberRepository articleMemberRepository;

    private final MatchConditionMatchRepository matchConditionMatchRepository;
    private final MatchRepository matchRepository;
    private final MatchMemberRepository matchMemberRepository;

    private final ActivityRepository activityRepository;


    private final SlackBotService slackBotService;

    private final MemberMapper memberMapper;
    private final MatchConditionMapper matchConditionMapper;

    @Transactional
    public ArticleOnlyIdResponse createArticle(String userId, Article.ArticleDto articleRequest) {
        Member member = userRepository.findByApiId(userId).orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND)).getMember();
        Article article = articleRepository.save(
            Article.of(articleRequest.getDate(),
                articleRequest.getTitle(),
                articleRequest.getContent(),
                articleRequest.getAnonymity(),
                articleRequest.getParticipantNumMax(),
                articleRequest.getContentCategory()));

        ArticleMember articleMemberAuthor = articleMemberRepository.save(
            ArticleMember.of(member, true, article));

        List<ArticleMatchCondition> articleMatchConditionList = allMatchConditionToArticleMatchCondition(
            articleRequest, article);
        articleMatchConditionRepository.saveAll(articleMatchConditionList);
        return ArticleOnlyIdResponse.of(article.getApiId());
    }

    @Transactional
    public ArticleOnlyIdResponse deleteArticle(String articleId) {

        articleRepository.deleteByApiId(articleId);

        return ArticleOnlyIdResponse.of(articleId);
    }

    @Transactional
    public ArticleOnlyIdResponse updateArticle(ArticleDto articleRequest, String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        //기존 조건 삭제
        article.getArticleMatchConditions().clear();
        articleMatchConditionRepository.deleteAll(article.getArticleMatchConditions());

        //새로운 조건 객체 생성
        List<ArticleMatchCondition> articleMatchConditions = allMatchConditionToArticleMatchCondition(
            articleRequest, article);

        //article delete, match여부, participantNumMax적정한지 확인.
        article.update(articleRequest.getDate(), articleRequest.getTitle(),
            articleRequest.getContent(),
            articleRequest.getParticipantNumMax(), articleMatchConditions);
        articleMatchConditionRepository.saveAll(articleMatchConditions);
        return ArticleOnlyIdResponse.of(article.getApiId());
    }


    public ArticleReadOneResponse readOneArticle(String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        List<MemberDto> memberDtos = article.getArticleMembers().stream()
            .map(am -> (
                memberMapper.entityToMemberDto(am.getMember(), am)))
            .collect(Collectors.toList());

        List<MatchCondition> matchConditions = article.getArticleMatchConditions().stream()
            .map(amc ->
                amc.getMatchCondition())
            .collect(Collectors.toList());

        return ArticleReadOneResponse.of(article, memberDtos,
            MatchConditionDto.of(Place.extractPlaceFromMatchCondition(matchConditions),
                TimeOfEating.extractTimeOfEatingFromMatchCondition(matchConditions),
                WayOfEating.extractWayOfEatingFromMatchCondition(matchConditions),
                TypeOfStudy.extractTypeOfStudyFromMatchCondition(matchConditions)
            ));

    }

    public Slice<ArticleReadResponse> readAllArticle(Pageable pageable,
        ArticleSearch condition) {
        return articleRepository.findSliceByCondition(pageable,
            condition).map((article) -> {
            List<MatchCondition> matchConditions = article.getArticleMatchConditions().stream()
                .map(amc ->
                    amc.getMatchCondition())
                .collect(Collectors.toList());
                return ArticleReadResponse.of(article, MatchConditionDto.of(Place.extractPlaceFromMatchCondition(matchConditions),
                    TimeOfEating.extractTimeOfEatingFromMatchCondition(matchConditions),
                    WayOfEating.extractWayOfEatingFromMatchCondition(matchConditions),
                    TypeOfStudy.extractTypeOfStudyFromMatchCondition(matchConditions)
                ));
            }
        );
    }

    //이미 참여중인 경우 방지.
    @Transactional
    public ArticleOnlyIdResponse participateArticle(String userId, String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        Member member = userRepository.findByApiId(userId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND)).getMember();

        ArticleMember participateMember = article.participateMember(member);

        articleMemberRepository.save(participateMember);
        return ArticleOnlyIdResponse.of(article.getApiId());
    }

    @Transactional
    public ArticleOnlyIdResponse participateCancelArticle(String userId, String articleId) {

        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        Member member = userRepository.findByApiId(userId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
            .getMember();

        ArticleMember participateMember = article.participateCancelMember(member);
        articleMemberRepository.delete(participateMember);
        return ArticleOnlyIdResponse.of(article.getApiId());
    }

    @Transactional
    public ArticleOnlyIdResponse completeArticle(String userId, String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        Member requestMember = userRepository.findByApiId(userId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
            .getMember();
        Member memberAuthor = article.getAuthorMember();
        //글 작성자아닌 경우
        if (!requestMember.equals(memberAuthor)) {
            throw new NotAuthorException(ErrorCode.NOT_ARTICLE_AUTHOR);
        }
        //글이 이미 삭제된 경우,
        article.complete();
        //매칭 완료
        Match match = matchRepository.save(
            Match.of(MatchStatus.MATCHED, article.getContentCategory(), MethodCategory.MANUAL,
                article, article.getParticipantNum()));
        matchConditionMatchRepository.saveAll(article.getArticleMatchConditions().stream()
            .map(arm ->
                MatchConditionMatch.of(match, arm.getMatchCondition()))
            .collect(Collectors.toList()));
        matchMemberRepository.saveAll(article.getArticleMembers().stream()
            .map(am ->
                MatchMember.of(match, am.getMember(), am.getIsAuthor()))
            .collect(Collectors.toList()));
        //활동 점수 부여


        article.getArticleMembers()
            .forEach(am ->{
                if (am.getIsAuthor()){
                    activityRepository.save(
                        Activity.of(am.getMember(), match, ActivityType.ARTICLE_AUTHOR_MATCH.getScore(),
                            article.getContentCategory(), ActivityType.ARTICLE_AUTHOR_MATCH));
                } else{
                    activityRepository.save(
                        Activity.of(am.getMember(), match, ActivityType.ARTICLE_PARTICIPANT_MATCH.getScore(),
                            article.getContentCategory(), ActivityType.ARTICLE_PARTICIPANT_MATCH));
                }
            }
        );
        //슬랙 알림(비동기)

        ArrayList<String> slackIds = new ArrayList<>();
        for (ArticleMember articleMember : article.getArticleMembers()) {
            Optional<String> slackId = slackBotService.getSlackIdByEmail(
                articleMember.getMember().getUser().getEmail());
            if (slackId.isPresent()) {
                slackIds.add(slackId.get());
            }
        }

        String MPIMId = slackBotService.createMPIM(slackIds)
            .orElseThrow(() -> new SlackException(ErrorCode.SLACK_ERROR));
        slackBotService.sendMessage(MPIMId, "매칭이 완료되었습니다. 대화방에서 매칭을 확인해주세요.\n"
            + "만약, 초대 되지않은 유저가 있다면 slack에서 초대해주세요.\n"
            + "slack에 등록된 email이 IntraId" + User.SEOUL_42
            + " 형식으로 되어있지 않으면 초대 및 알림이 발송 되지 않을 수 있습니다.");
        return ArticleOnlyIdResponse.of(article.getApiId());
    }


    private List<String> allMatchConditionToStringList(Article.ArticleDto articleRequest) {
        List<String> matchConditionStrings = new ArrayList<>();
        List<Place> place = articleRequest.getMatchConditionDto().getPlaceList();
        if (place == null) {
            place = new ArrayList<>();
        }
        matchConditionStrings.addAll(place.stream()
            .map(Enum::name)
            .collect(Collectors.toList()));
        List<TimeOfEating> timeOfEating = articleRequest.getMatchConditionDto().getTimeOfEatingList();
        if (timeOfEating == null) {
            timeOfEating = new ArrayList<>();
        }
        matchConditionStrings.addAll(timeOfEating.stream()
            .map(Enum::name)
            .collect(Collectors.toList()));

        List<WayOfEating> wayOfEating = articleRequest.getMatchConditionDto().getWayOfEatingList();
        if (wayOfEating == null) {
            wayOfEating = new ArrayList<>();
        }
        matchConditionStrings.addAll(wayOfEating.stream()
            .map(Enum::name)
            .collect(Collectors.toList()));

        List<TypeOfStudy> typeOfStudy = articleRequest.getMatchConditionDto().getTypeOfStudyList();
        if (typeOfStudy == null) {
            typeOfStudy = new ArrayList<>();
        }
        matchConditionStrings.addAll(typeOfStudy.stream()
            .map(Enum::name)
            .collect(Collectors.toList()));

        return matchConditionStrings;
    }

    private List<ArticleMatchCondition> allMatchConditionToArticleMatchCondition(
        Article.ArticleDto articleRequest, Article article) {
        return allMatchConditionToStringList(articleRequest).stream()
            .map((matchConditionString) ->
                matchConditionRepository.findByValue(matchConditionString).orElseThrow(() ->
                    new NoEntityException(ErrorCode.ENTITY_NOT_FOUND)
                ))
            .map((matchCondition) ->
                ArticleMatchCondition.of(matchCondition, article))
            .collect(Collectors.toList());
    }

}
