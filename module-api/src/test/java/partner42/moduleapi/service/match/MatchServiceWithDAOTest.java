package partner42.moduleapi.service.match;


import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import partner42.moduleapi.TestBootstrapConfig;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.dto.match.MatchDto;
import partner42.moduleapi.dto.match.MatchReviewRequest;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.moduleapi.dto.member.MemberReviewDto;
import partner42.moduleapi.mapper.MemberMapperImpl;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.BusinessException;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.activity.ActivityRepository;
import partner42.modulecommon.repository.activity.ActivitySearch;
import partner42.modulecommon.repository.match.MatchMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.match.MatchSearch;
import partner42.modulecommon.repository.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MatchService.class, MemberMapperImpl.class,
    Auditor.class, QuerydslConfig.class, JpaAndEntityPackagePathConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class})
class MatchServiceWithDAOTest {
    @Autowired
    private MatchService matchService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private MatchMemberRepository matchMemberRepository;

    @Test
    void readOneMatch_whenUserIsContainedInMatch_thenReturn() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);
        Match match2 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);
        Match match1Save = matchRepository.save(match1);
        Match match2Save = matchRepository.save(match2);

        MatchMember mm1 = MatchMember.of(match1, takim.getMember(), true);
        MatchMember mm2 = MatchMember.of(match2, takim.getMember(), false);
        MatchMember mm3 = MatchMember.of(match1, sorkim.getMember(), false);
        matchMemberRepository.saveAll(List.of(mm1, mm2, mm3));
        //when
        MatchDto matchDtoTakim = matchService.readOneMatch(takim.getUsername(),
            match1Save.getApiId());

        //then
        assertThat(matchDtoTakim).usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo(MatchDto.of(match1Save, MatchConditionDto.builder().build(), List.of(
                MemberDto.builder()
                    .nickname(takim.getMember().getNickname())
                    .isAuthor(true)
                    .isMe(true)
                    .build(),
                MemberDto.builder()
                    .nickname(sorkim.getMember().getNickname())
                    .isAuthor(false)
                    .isMe(false)
                    .build()), false));

    }

    @Test
    void readOneMatch_whenUserIsNotContainedInMatch_thenThrow() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);
        Match match2 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);
        Match match1Save = matchRepository.save(match1);
        Match match2Save = matchRepository.save(match2);

        MatchMember mm1 = MatchMember.of(match1, takim.getMember(), true);
        MatchMember mm2 = MatchMember.of(match2, takim.getMember(), false);
        MatchMember mm3 = MatchMember.of(match1, sorkim.getMember(), false);
        matchMemberRepository.saveAll(List.of(mm1, mm2, mm3));
        //then
        assertThatThrownBy(() ->
            matchService.readOneMatch(hyenam.getUsername(), match1Save.getApiId()))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void readMyMatches_givenMatches_whenFindByMatchSearch_ThenReturn() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);
        Match matchStudy = Match.of(MatchStatus.MATCHED, ContentCategory.STUDY,
            MethodCategory.MANUAL, null,
            3);
        Match match3 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);
        Match matchRandom = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL,
            MethodCategory.RANDOM, null,
            3);
        Match matchSorkim = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL,
            MethodCategory.MANUAL, null,
            3);
        matchRepository.saveAll(List.of(match1, matchStudy, match3, matchRandom, matchSorkim));

        MatchMember mm1 = MatchMember.of(match1, takim.getMember(), true);
        MatchMember mm2 = MatchMember.of(matchStudy, takim.getMember(), false);
        MatchMember mm3 = MatchMember.of(match3, takim.getMember(), false);
        MatchMember mm4 = MatchMember.of(matchRandom, takim.getMember(), false);
        MatchMember mm5 = MatchMember.of(matchSorkim, sorkim.getMember(), false);
        MatchMember mm6 = MatchMember.of(matchSorkim, takim.getMember(), true);

        matchMemberRepository.saveAll(List.of(mm1, mm2, mm3, mm4, mm5, mm6));

        //when
        MatchSearch matchSearch = new MatchSearch();
        matchSearch.setContentCategory(ContentCategory.MEAL);
        matchSearch.setMethodCategory(MethodCategory.MANUAL);
        Slice<MatchDto> matchDtos = matchService.readMyMatches(takim.getUsername(), matchSearch,
            PageRequest.of(0, 10, Sort.by(
                Order.asc("createdAt"))));
        //then
        assertThat(matchDtos.getContent()).hasSize(3);
        assertThat(matchDtos.getContent().get(0)).usingRecursiveComparison()
            .ignoringAllOverriddenEquals().isEqualTo(
                MatchDto.of(match1, MatchConditionDto.builder().build(), List.of(
                    MemberDto.builder()
                        .nickname(takim.getMember().getNickname())
                        .isAuthor(true)
                        .isMe(true)
                        .build()), false));
        assertThat(matchDtos.getContent().get(1)).usingRecursiveComparison()
            .ignoringAllOverriddenEquals().isEqualTo(
                MatchDto.of(match3, MatchConditionDto.builder().build(), List.of(
                    MemberDto.builder()
                        .nickname(takim.getMember().getNickname())
                        .isAuthor(false)
                        .isMe(true)
                        .build()), false));
        assertThat(matchDtos.getContent().get(2).getParticipantsOrAuthor())
            .containsOnly(
                MemberDto.builder()
                    .nickname(takim.getMember().getNickname())
                    .isAuthor(true)
                    .isMe(true)
                    .build(),
                MemberDto.builder()
                    .nickname(sorkim.getMember().getNickname())
                    .isAuthor(false)
                    .isMe(false)
                    .build());

    }

    @Test
    void makeReview_whenReview_thenActivityScoreSumAndReviewFlagIsEqualTo() {

        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);

        Match match1Save = matchRepository.save(match1);

        MatchMember mm1 = MatchMember.of(match1, takim.getMember(), true);
        MatchMember mm2 = MatchMember.of(match1, sorkim.getMember(), false);
        MatchMember mm3 = MatchMember.of(match1, hyenam.getMember(), false);

        matchMemberRepository.saveAll(List.of(mm1, mm2, mm3));

        //when
        matchService.makeReview(takim.getUsername(), match1.getApiId(), MatchReviewRequest.builder()
            .matchId(match1Save.getApiId())
            .memberReviewDtos(
                List.of(
                    MemberReviewDto.builder()
                        .nickname("sorkim")
                        .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_1).build(),
                    MemberReviewDto.builder()
                        .nickname("hyenam")
                        .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_2).build()))
            .build());

        Match match = matchRepository.findByApiId(match1Save.getApiId()).get();
        Integer takimSum = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
                takim.getMember().getId(),
                new ActivitySearch()).stream()
            .map(ActivityMatchScore::getScore)
            .reduce(0, Integer::sum);
        Integer sorkimSum = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
                sorkim.getMember().getId(),
                new ActivitySearch()).stream()
            .map(ActivityMatchScore::getScore)
            .reduce(0, Integer::sum);
        Integer hyenamSum = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
                hyenam.getMember().getId(),
                new ActivitySearch()).stream()
            .map(ActivityMatchScore::getScore)
            .reduce(0, Integer::sum);
        //then
        assertThat(takimSum).isEqualTo(ActivityMatchScore.MAKE_MATCH_REVIEW.getScore());
        assertThat(sorkimSum).isEqualTo(ActivityMatchScore.MATCH_REVIEW_1.getScore());
        assertThat(hyenamSum).isEqualTo(ActivityMatchScore.MATCH_REVIEW_2.getScore());
        assertThat(match.getMatchMembers())
            .extracting(MatchMember::getIsReviewed)
            .containsExactlyInAnyOrder(true, false, false);
        assertThat(match.getMatchMembers().stream()
            .filter(matchMember -> matchMember.getMember().getNickname().equals("takim"))
            .findFirst().get().getIsReviewed()).isTrue();

        assertThatThrownBy(() ->
            matchService.makeReview(takim.getUsername(), match1.getApiId(),
                MatchReviewRequest.builder()
                    .matchId(match1Save.getApiId())
                    .memberReviewDtos(
                        List.of(
                            MemberReviewDto.builder()
                                .nickname("sorkim")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_1).build(),
                            MemberReviewDto.builder()
                                .nickname("hyenam")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_2).build()))
                    .build())).isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.ALREADY_REVIEWED.getMessage());
    }

    @Test
    void makeReview_whenMakeReviewRedundantBySameUser_thenThrow() {

        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);

        Match match1Save = matchRepository.save(match1);

        MatchMember mm1 = MatchMember.of(match1, takim.getMember(), true);
        MatchMember mm2 = MatchMember.of(match1, sorkim.getMember(), false);
        MatchMember mm3 = MatchMember.of(match1, hyenam.getMember(), false);

        matchMemberRepository.saveAll(List.of(mm1, mm2, mm3));

        //when
        matchService.makeReview(takim.getUsername(), match1.getApiId(), MatchReviewRequest.builder()
            .matchId(match1Save.getApiId())
            .memberReviewDtos(
                List.of(
                    MemberReviewDto.builder()
                        .nickname("sorkim")
                        .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_1).build(),
                    MemberReviewDto.builder()
                        .nickname("hyenam")
                        .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_2).build()))
            .build());
        //then
        assertThatThrownBy(() ->
            matchService.makeReview(takim.getUsername(), match1.getApiId(),
                MatchReviewRequest.builder()
                    .matchId(match1Save.getApiId())
                    .memberReviewDtos(
                        List.of(
                            MemberReviewDto.builder()
                                .nickname("sorkim")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_1).build(),
                            MemberReviewDto.builder()
                                .nickname("hyenam")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_2).build()))
                    .build())).isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.ALREADY_REVIEWED.getMessage());
    }

    @Test
    void makeReview_whenReviewedMemberIsNotInParticipatedOrReviewingSelf_thenThrow() {

        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
            null,
            3);

        Match match1Save = matchRepository.save(match1);

        MatchMember mm1 = MatchMember.of(match1, takim.getMember(), true);
        MatchMember mm2 = MatchMember.of(match1, sorkim.getMember(), false);

        matchMemberRepository.saveAll(List.of(mm1, mm2));

        //then
        //존재하지 않는 유저 요청
        assertThatThrownBy(() ->
            matchService.makeReview(takim.getUsername(), match1.getApiId(),
                MatchReviewRequest.builder()
                    .matchId(match1Save.getApiId())
                    .memberReviewDtos(
                        List.of(
                            MemberReviewDto.builder()
                                .nickname("notParticipatedMember")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_1).build(),
                            MemberReviewDto.builder()
                                .nickname("sorkim")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_2).build()))
                    .build())).isInstanceOf(NoEntityException.class);

        //자기 자신
        assertThatThrownBy(() ->
            matchService.makeReview(takim.getUsername(), match1.getApiId(),
                MatchReviewRequest.builder()
                    .matchId(match1Save.getApiId())
                    .memberReviewDtos(
                        List.of(
                            MemberReviewDto.builder()
                                .nickname("takim")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_1).build()
                        ))
                    .build())).isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.REVIEWING_SELF.getMessage());

        //회원에느 member가 있지만 match에는 없는 경우
        assertThatThrownBy(() ->
            matchService.makeReview(takim.getUsername(), match1.getApiId(),
                MatchReviewRequest.builder()
                    .matchId(match1Save.getApiId())
                    .memberReviewDtos(
                        List.of(
                            MemberReviewDto.builder()
                                .nickname("hyenam")
                                .activityMatchScore(ActivityMatchScore.MATCH_REVIEW_2).build()))
                    .build())).isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.REVIEWED_MEMBER_NOT_IN_MATCH.getMessage());

    }
}