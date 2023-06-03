package partner42.moduleapi.service.article;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.EmailDto;
import partner42.moduleapi.dto.alarm.ResponseWithAlarmEventDto;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.article.ArticleReadOneResponse;
import partner42.moduleapi.dto.article.ArticleReadResponse;
import partner42.moduleapi.dto.match.MatchOnlyIdResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.moduleapi.mapper.MemberMapper;
import partner42.moduleapi.config.kafka.AlarmEvent;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.NoEntityException;
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

        articleMemberRepository.save(ArticleMember.of(member, true, article));

        List<ArticleMatchCondition> articleMatchConditionList = matchConditionRepository.findByValueIn(
                allMatchConditionToStringList(articleRequest.getMatchConditionDto())).stream()
            .map((matchCondition) ->
                ArticleMatchCondition.of(matchCondition, article))
            .collect(Collectors.toList());
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
        User user = getUserByUsernameOrException(username);
        Article article = articleRepository.findByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        verifyUserIsArticleAuthorMemberOrAdmin(user, article);
        articleRepository.deleteByApiId(articleId);

        return ArticleOnlyIdResponse.of(articleId);
    }

    //OptimisticLockException
    @Transactional
    public ArticleOnlyIdResponse softDelete(String username, String articleId) {

        User user = getUserByUsernameOrException(username);
        Article article = articleRepository.findByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        verifyUserIsArticleAuthorMemberOrAdmin(user, article);
        article.recoverableDelete();
        return ArticleOnlyIdResponse.of(articleId);
    }

    //OptimisticLockException
    @Transactional
    public ArticleOnlyIdResponse updateArticle(ArticleDto articleRequest, String username,
        String articleId) {

        User user = getUserByUsernameOrException(username);
        Article article = articleRepository.findByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        verifyUserIsArticleAuthorMemberOrAdmin(user, article);

        //기존 조건 삭제
        articleMatchConditionRepository.deleteAll(article.getArticleMatchConditions());
        article.getArticleMatchConditions().clear();

        //새로운 조건 객체 생성
        List<ArticleMatchCondition> articleMatchConditions = matchConditionRepository.findByValueIn(
                allMatchConditionToStringList(articleRequest.getMatchConditionDto())).stream()
            .map((matchCondition) ->
                ArticleMatchCondition.of(matchCondition, article))
            .collect(Collectors.toList());

        articleMatchConditionRepository.saveAll(articleMatchConditions);

        //article delete, match여부, participantNumMax적정한지 확인.
        article.update(articleRequest.getDate(), articleRequest.getTitle(),
            articleRequest.getContent(), articleRequest.getAnonymity(),
            articleRequest.getParticipantNumMax(), articleRequest.getContentCategory(),
            articleMatchConditions);
        return ArticleOnlyIdResponse.of(article.getApiId());
    }


    public ArticleReadOneResponse readOneArticle(String username, String articleId) {
        Member member =
            username == null ? null : getUserByUsernameOrException(username).getMember();
        Article article = articleRepository.findEntityGraphArticleMatchConditionsByApiIdAndIsDeletedIsFalse(
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

//    @Cacheable(value = "articles", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
//        + " + '-' + #condition.anonymity + '-' + #condition.contentCategory +'-'+ #condition.isComplete")
        public SliceImpl<ArticleReadResponse>readAllArticle(Pageable pageable,
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
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ResponseWithAlarmEventDto<ArticleOnlyIdResponse> participateArticle(String username, String articleId) {
        Article article = articleRepository.findEntityGraphArticleMembersByApiIdAndIsDeletedIsFalse(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        User user = getUserByUsernameOrException(username);
        Member member = user.getMember();

        ArticleMember participateMember = article.participateMember(member);
        articleMemberRepository.save(participateMember);
        // 알림 생성
        AlarmEvent alarmEvent = new AlarmEvent(AlarmType.PARTICIPATION_ON_MY_POST,
            AlarmArgs.builder()
                .opinionId(null)
                .articleId(articleId)
                .callingMemberNickname(member.getNickname())
                .build(), article.getAuthorMember().getUser().getId(), SseEventName.ALARM_LIST);
        return ResponseWithAlarmEventDto.<ArticleOnlyIdResponse>builder()
            .response(ArticleOnlyIdResponse.of(articleId))
            .alarmEvent(alarmEvent)
            .build();
    }

    //OptimisticLockException
    @Transactional
    public ResponseWithAlarmEventDto<ArticleOnlyIdResponse> participateCancelArticle(String username, String articleId) {

        Article article = articleRepository.findEntityGraphArticleMembersByApiIdAndIsDeletedIsFalse(
                articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));

        User user = getUserByUsernameOrException(username);
        Member member = user.getMember();

        ArticleMember participateMember = article.participateCancelMember(member);
        articleMemberRepository.delete(participateMember);

        AlarmEvent alarmEvent = new AlarmEvent(AlarmType.PARTICIPATION_CANCEL_ON_MY_POST,
            AlarmArgs.builder()
                .opinionId(null)
                .articleId(articleId)
                .callingMemberNickname(member.getNickname())
                .build(), article.getAuthorMember().getUser().getId(), SseEventName.ALARM_LIST);

        ResponseWithAlarmEventDto<ArticleOnlyIdResponse> responseWithAlarmEventDto =
            ResponseWithAlarmEventDto.<ArticleOnlyIdResponse>builder()
                .response(ArticleOnlyIdResponse.of(articleId))
                .alarmEvent(alarmEvent)
                .build();
        return responseWithAlarmEventDto;
    }

    //OptimisticLockException
    @Transactional
    public EmailDto<MatchOnlyIdResponse> completeArticle(String username, String articleId) {
        //글 작성자아닌 경우
        User user = getUserByUsernameOrException(username);
        Article article = articleRepository.findByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        verifyUserIsArticleAuthorMemberOrAdmin(user, article);
        //매칭 완료
        Match match = article.completeArticleWhenMatchDecided();
        matchRepository.save(match);
        //활동 점수 부여
        List<Activity> activities = article.activityScoreToParticipatingMember();
        activityRepository.saveAll(activities);
        // 알림 생성
        //sse
        Member authorMember = article.getAuthorMember();
        List<AlarmEvent> alarmEventList = article.getArticleMembers().stream()
            .map(am ->
                new AlarmEvent(AlarmType.MATCH_CONFIRMED, AlarmArgs.builder()
                    .opinionId(null)
                    .articleId(articleId)
                    .callingMemberNickname(authorMember.getNickname())
                    .build(), am.getMember().getUser().getId(), SseEventName.ALARM_LIST))
            .collect(Collectors.toList());

        return EmailDto.<MatchOnlyIdResponse>builder()
            .emails(article.getArticleMembers().stream()
                .map(am -> am.getMember().getUser().getEmail())
                .collect(Collectors.toList()))
            .response(MatchOnlyIdResponse.builder()
                .matchId(match.getApiId())
                .build())
            .alarmEventList(alarmEventList)
            .build();
    }

    private void verifyUserIsArticleAuthorMemberOrAdmin(User user, Article article) {
        if (user.hasRole(RoleEnum.ROLE_ADMIN)) {
            return ;
        }
        if (!article.isAuthorMember(user.getMember())) {
            throw new InvalidInputException(ErrorCode.NOT_ARTICLE_AUTHOR);
        }
    }

    private List<String> allMatchConditionToStringList(MatchConditionDto matchConditionDto) {
        List<String> matchConditionStrings = new ArrayList<>();
        if (matchConditionDto.getPlaceList() != null) {
            matchConditionStrings.addAll(matchConditionDto.getPlaceList().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        }
        if (matchConditionDto.getTimeOfEatingList() != null) {
            matchConditionStrings.addAll(matchConditionDto
                .getTimeOfEatingList().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        }
        if (matchConditionDto.getWayOfEatingList() != null) {
            matchConditionStrings.addAll(matchConditionDto.getWayOfEatingList().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        }
        if (matchConditionDto.getTypeOfStudyList() != null) {
            matchConditionStrings.addAll(matchConditionDto.getTypeOfStudyList().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        }
        return matchConditionStrings;
    }
}
