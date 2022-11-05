package com.seoul.openproject.partner.service.article;

import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import com.seoul.openproject.partner.domain.model.match.Match;
import com.seoul.openproject.partner.domain.model.match.MatchCondition;
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
import com.seoul.openproject.partner.domain.model.article.Place;
import com.seoul.openproject.partner.domain.model.article.TimeOfEating;
import com.seoul.openproject.partner.domain.model.article.WayOfEating;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchConditionMatch;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.domain.model.user.User;
import com.seoul.openproject.partner.exception.NotAuthorException;
import com.seoul.openproject.partner.exception.SlackBotException;
import com.seoul.openproject.partner.repository.article.ArticleRepository;
import com.seoul.openproject.partner.repository.article.ArticleSearch;
import com.seoul.openproject.partner.repository.articlemember.ArticleMemberRepository;
import com.seoul.openproject.partner.repository.match.MatchMemberRepository;
import com.seoul.openproject.partner.repository.match.MatchRepository;
import com.seoul.openproject.partner.repository.matchcondition.MatchConditionMatchRepository;
import com.seoul.openproject.partner.repository.matchcondition.MatchConditionRepository;
import com.seoul.openproject.partner.repository.member.MemberRepository;
import com.seoul.openproject.partner.repository.ArticleMatchConditionRepository;
import com.seoul.openproject.partner.mapper.MatchConditionMapper;
import com.seoul.openproject.partner.mapper.MemberMapper;
import com.seoul.openproject.partner.repository.user.UserRepository;
import com.seoul.openproject.partner.service.slack.SlackBotService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
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

    private final SlackBotService slackBotService;

    private final MemberMapper memberMapper;
    private final MatchConditionMapper matchConditionMapper;

    @Transactional
    public ArticleOnlyIdResponse createArticle(Article.ArticleDto articleRequest) {
        String memberId = articleRequest.getMemberId();
        Member member = memberRepository.findByApiId(memberId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{memberId, Member.class.toString()}, Locale.KOREAN)));

        ArticleMember articleMemberAuthor = ArticleMember.of(member, true);
        List<ArticleMatchCondition> articleMatchConditionList = allMatchConditionToArticleMatchCondition(
            articleRequest);

        Article article = articleRepository.save(
            Article.of(articleRequest.getDate(),
                articleRequest.getTitle(),
                articleRequest.getContent(),
                articleRequest.getAnonymity(),
                articleRequest.getParticipantNumMax(),
                articleRequest.getContentCategory(),
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
        Article article = articleRepository.findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{articleId, Article.class.toString()}, Locale.KOREAN)));
        List<ArticleMatchCondition> articleMatchConditions = allMatchConditionToArticleMatchCondition(
            articleRequest);
        articleMatchConditionRepository.deleteAll(article.getArticleMatchConditions());
        if (article.isParticipantNumMaxChangeable(articleRequest.getParticipantNumMax())) {
            article.update(articleRequest.getDate(), articleRequest.getTitle(),
                articleRequest.getContent(),
                articleRequest.getParticipantNumMax(), articleMatchConditions);
        } else {
            throw new IllegalArgumentException("최대참여자 수가 현재 참여자 수보다 적기 때문에 변경할 수 없습니다.");
        }
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

    private List<ArticleMatchCondition> allMatchConditionToArticleMatchCondition(
        Article.ArticleDto articleRequest) {
        return allMatchConditionToStringList(articleRequest).stream()
            .map((matchConditionString) ->
                matchConditionRepository.findByValue(matchConditionString).orElseThrow(() ->
                    new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{matchConditionString, MatchCondition.class.toString()}, Locale.KOREAN))
                ))
            .map(ArticleMatchCondition::of)
            .collect(Collectors.toList());
    }


    public ArticleReadOneResponse readOneArticle(String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{articleId, Article.class.toString()}, Locale.KOREAN)));

        return ArticleReadOneResponse.of(article, memberMapper, matchConditionMapper);

    }

    public Slice<ArticleReadResponse> readAllArticle(Pageable pageable,
        ArticleSearch condition) {
        return articleRepository.findSliceByCondition(pageable,
            condition).map((article) ->
                ArticleReadResponse.of(article, matchConditionMapper)
        );
    }

    //이미 참여중인 경우 방지.
    @Transactional
    public ArticleOnlyIdResponse participateArticle(String userId, String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMembersByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{articleId, Article.class.toString()}, Locale.KOREAN)));

        ArticleMember participateMember = article.participate(userRepository.findByApiId(userId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{userId, User.class.toString()}, Locale.KOREAN))).getMember());
        articleMemberRepository.save(participateMember);
        return ArticleOnlyIdResponse.builder()
            .articleId(article.getApiId())
            .build();
    }

    @Transactional
    public ArticleOnlyIdResponse participateCancelArticle(String userId, String articleId) {

        Article article = articleRepository.findDistinctFetchArticleMembersByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{articleId, Article.class.toString()}, Locale.KOREAN)));

        ArticleMember participateMember = article.participateCancel(userRepository.findByApiId(userId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{userId, User.class.toString()}, Locale.KOREAN))).getMember());
        articleMemberRepository.delete(participateMember);
        return ArticleOnlyIdResponse.builder()
            .articleId(article.getApiId())
            .build();
    }

    @Transactional
    public ArticleOnlyIdResponse completeArticle(String userId, String articleId) {
        Article article = articleRepository.findDistinctFetchArticleMembersByApiIdAndIsDeletedIsFalse(articleId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{articleId, Article.class.toString()}, Locale.KOREAN)));

        Member requestMember = userRepository.findByApiId(userId)
            .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("exception.notfound", new Object[]{userId, User.class.toString()}, Locale.KOREAN)))
            .getMember();
        Member memberAuthor = article.getAuthorMember();
        //글 작성자아닌 경우
        if (!requestMember.equals(memberAuthor)) {
            throw new NotAuthorException("글 작성자만 완료할 수 있습니다.");
        }
        article.complete();
        //매칭 완료
        Match match = matchRepository.save(Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            article));
        matchConditionMatchRepository.saveAll(article.getArticleMatchConditions().stream()
            .map(arm ->
                MatchConditionMatch.of(match, arm.getMatchCondition()))
            .collect(Collectors.toList()));
        matchMemberRepository.saveAll(article.getArticleMembers().stream()
            .map(am ->
                MatchMember.of(match, am.getMember(), am.getIsAuthor()))
            .collect(Collectors.toList()));
        //활동 점수 부여

        //슬랙 알림(비동기)

        ArrayList<String> slackIds= new ArrayList<>();
        for (ArticleMember articleMember : article.getArticleMembers()) {
            Optional<String> slackId = slackBotService.getSlackIdByEmail(
                articleMember.getMember().getUser().getEmail());
            if (slackId.isPresent()){
                slackIds.add(slackId.get());
            }
        }

        String MPIMId = slackBotService.createMPIM(slackIds)
            .orElseThrow(() -> new SlackBotException("슬랙 대화방 생성 실패"));
        slackBotService.sendMessage(MPIMId, "매칭이 완료되었습니다. 대화방에서 매칭을 확인해주세요.\n"
            + "만약, 초대 되지않은 유저가 있다면 slack에서 초대해주세요.\n"
            + "slack에 등록된 email이 IntraId" + User.SEOUL_42
            + " 형식으로 되어있지 않으면 초대 및 알림이 발송 되지 않을 수 있습니다.");
        return ArticleOnlyIdResponse.builder()
            .articleId(article.getApiId())
            .build();
    }
}
