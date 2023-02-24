package partner42.moduleapi.service.article;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.EmailDto;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.article.ArticleReadOneResponse;
import partner42.moduleapi.dto.article.ArticleReadResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.moduleapi.mapper.MatchConditionMapper;
import partner42.moduleapi.mapper.MemberMapper;
import partner42.moduleapi.service.alarm.AlarmService;
import partner42.modulecommon.config.kafka.AlarmEvent;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.activity.ActivityType;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.NotAuthorException;
import partner42.modulecommon.producer.AlarmProducer;
import partner42.modulecommon.repository.activity.ActivityRepository;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.article.ArticleSearch;
import partner42.modulecommon.repository.articlemember.ArticleMemberRepository;
import partner42.modulecommon.repository.match.MatchMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.ArticleMatchConditionRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionMatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.UserRepository;

@Slf4j
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

    private final MemberMapper memberMapper;
    private final MatchConditionMapper matchConditionMapper;

    private final AlarmService alarmService;

    private final AlarmProducer alarmProducer;
    @Transactional
    public ArticleOnlyIdResponse createArticle(String username, ArticleDto articleRequest) {
        Member member = getUserByUsernameOrException(username).getMember();
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

    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND));
    }

    //OptimisticLockException
    @Transactional
    public ArticleOnlyIdResponse deleteArticle(String username, String articleId) {
        verifyAuthorOfArticle(username, articleId);
        articleRepository.deleteByApiId(articleId);

        return ArticleOnlyIdResponse.of(articleId);
    }

    //OptimisticLockException
    @Transactional
    public ArticleOnlyIdResponse changeIsDelete(String username, String articleId) {

        verifyAuthorOfArticle(username, articleId);
        Article article = articleRepository.findByApiId(articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        article.recoverableDelete();
        return ArticleOnlyIdResponse.of(articleId);
    }

    //OptimisticLockException
    @Transactional
    public ArticleOnlyIdResponse updateArticle(ArticleDto articleRequest, String username,
        String articleId) {

        verifyAuthorOfArticle(username, articleId);

        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        //기존 조건 삭제
        articleMatchConditionRepository.deleteAll(article.getArticleMatchConditions());
        article.getArticleMatchConditions().clear();

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


    public ArticleReadOneResponse readOneArticle(String username, String articleId) {
        Member member =
            username == null ? null : getUserByUsernameOrException(username).getMember();
        Article article = articleRepository.findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        List<MemberDto> memberDtos = article.getArticleMembers().stream()
            .map(am -> (
                memberMapper.articleMemberToMemberDto(am.getMember(), am,
                    am.getMember().equals(member))))
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

    public SliceImpl<ArticleReadResponse> readAllArticle(Pageable pageable,
        ArticleSearch condition) {
        Slice<Article> articleSlices = articleRepository.findSliceByCondition(pageable,
            condition);
        return new SliceImpl<>(articleSlices.getContent().stream()
            .map((article) -> {
                List<MatchCondition> matchConditions = article.getArticleMatchConditions().stream()
                    .map(amc ->
                        amc.getMatchCondition())
                    .collect(Collectors.toList());
                return ArticleReadResponse.of(article,
                    MatchConditionDto.of(Place.extractPlaceFromMatchCondition(matchConditions),
                        TimeOfEating.extractTimeOfEatingFromMatchCondition(matchConditions),
                        WayOfEating.extractWayOfEatingFromMatchCondition(matchConditions),
                        TypeOfStudy.extractTypeOfStudyFromMatchCondition(matchConditions)
                    ));
            })
            .collect(Collectors.toList()),
            articleSlices.getPageable(),
            articleSlices.hasNext());

    }

    //OptimisticLockException
    //이미 참여중인 경우 방지.
    @Transactional
    public ArticleOnlyIdResponse participateArticle(String username, String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        User user = getUserByUsernameOrException(username);
        Member member = user.getMember();

        ArticleMember participateMember = article.participateMember(member);

        articleMemberRepository.save(participateMember);

        // 알림 생성
        //sse event 생성.

        alarmProducer.send(new AlarmEvent(AlarmType.PARTICIPATION_ON_MY_POST, AlarmArgs.builder()
            .opinionId(null)
            .articleId(articleId)
            .callingMemberNickname(member.getNickname())
            .build(), article.getAuthorMember().getUser().getId(), SseEventName.ALARM_LIST));

        return ArticleOnlyIdResponse.of(article.getApiId());
    }

    //OptimisticLockException
    @Transactional
    public ArticleOnlyIdResponse participateCancelArticle(String username, String articleId) {

        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        User user = getUserByUsernameOrException(username);
        Member member = user.getMember();

        ArticleMember participateMember = article.participateCancelMember(member);
        articleMemberRepository.delete(participateMember);

        // 알림 생성
        //sse
        alarmProducer.send(new AlarmEvent(AlarmType.PARTICIPATION_CANCEL_ON_MY_POST, AlarmArgs.builder()
            .opinionId(null)
            .articleId(articleId)
            .callingMemberNickname(member.getNickname())
            .build(), article.getAuthorMember().getUser().getId(), SseEventName.ALARM_LIST));

        return ArticleOnlyIdResponse.of(article.getApiId());
    }

    //OptimisticLockException
    @Transactional
    public EmailDto<ArticleOnlyIdResponse> completeArticle(String username, String articleId) {
        //글 작성자아닌 경우
        verifyAuthorOfArticle(username, articleId);

        Article article = articleRepository.findDistinctFetchArticleMembersByApiId(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

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
            .forEach(am -> {
                    if (am.getIsAuthor()) {
                        activityRepository.save(
                            Activity.of(am.getMember(),
                                ActivityMatchScore.ARTICLE_MATCH_AUTHOR.getScore(),
                                article.getContentCategory(),
                                ActivityType.ARTICLE));
                    } else {
                        activityRepository.save(
                            Activity.of(am.getMember(),
                                ActivityMatchScore.MATCH_PARTICIPANT.getScore(),
                                article.getContentCategory(), ActivityType.MATCH));
                    }
                }
            );

        // 알림 생성
        //sse
        Member authorMember = article.getAuthorMember();
        for (ArticleMember articleMember : article.getArticleMembers()) {
            Member matchedMember = articleMember.getMember();
            alarmProducer.send(new AlarmEvent(AlarmType.MATCH_CONFIRMED, AlarmArgs.builder()
                .opinionId(null)
                .articleId(articleId)
                .callingMemberNickname(authorMember.getNickname())
                .build(), matchedMember.getUser().getId(), SseEventName.ALARM_LIST));
        }

        return EmailDto.<ArticleOnlyIdResponse>builder()
            .emails(article.getArticleMembers().stream()
                .map(am -> am.getMember().getUser().getEmail())
                .collect(Collectors.toList()))
            .response(ArticleOnlyIdResponse.of(article.getApiId()))
            .build();
    }

    private void verifyAuthorOfArticle(String username, String articleId) {

        User user = getUserByUsernameOrException(username);
        Article article = articleRepository.findByApiId(articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        if (!user.getUserRoles().stream()
            .map(UserRole::getRole)
            .map(Role::getValue)
            .collect(Collectors.toSet())
            .contains(RoleEnum.ROLE_ADMIN) &&
            !article.getAuthorMember().equals(user.getMember())) {
            throw new NotAuthorException(ErrorCode.NOT_ARTICLE_AUTHOR);
        }
    }

    private List<String> allMatchConditionToStringList(ArticleDto articleRequest) {
        List<String> matchConditionStrings = new ArrayList<>();
        List<Place> place = articleRequest.getMatchConditionDto().getPlaceList();
        if (place == null) {
            place = new ArrayList<>();
        }
        matchConditionStrings.addAll(place.stream()
            .map(Enum::name)
            .collect(Collectors.toList()));
        List<TimeOfEating> timeOfEating = articleRequest.getMatchConditionDto()
            .getTimeOfEatingList();
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
        ArticleDto articleRequest, Article article) {
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
