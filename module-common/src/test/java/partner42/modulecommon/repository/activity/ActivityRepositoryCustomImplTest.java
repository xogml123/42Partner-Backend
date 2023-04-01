package partner42.modulecommon.repository.activity;

import static org.assertj.core.api.Assertions.*;
import static partner42.modulecommon.domain.model.activity.ActivityMatchScore.ARTICLE_MATCH_AUTHOR;
import static partner42.modulecommon.domain.model.activity.ActivityMatchScore.MATCH_ABSENT;
import static partner42.modulecommon.domain.model.activity.ActivityMatchScore.MATCH_PARTICIPANT;
import static partner42.modulecommon.domain.model.activity.ActivityMatchScore.MATCH_REVIEW_1;
import static partner42.modulecommon.domain.model.activity.ActivityMatchScore.MATCH_REVIEW_2;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.repository.member.MemberRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QuerydslConfig.class, Auditor.class})
class ActivityRepositoryCustomImplTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ActivityRepository activityRepository;

    @Test
    void findActivityMatchScoreByMemberIdAndArticleSearch_givenActivitiesByActivitySearch_whenActivitySearchAndMemberIdDifferent_thenContainsOnly() {
        //given
        Member a = Member.of("a");
        Member b = Member.of("b");
        memberRepository.saveAll(List.of(a, b));
        Activity activity1 = Activity.of(a, ContentCategory.MEAL, ARTICLE_MATCH_AUTHOR);
        Activity activity2 = Activity.of(a, ContentCategory.MEAL, MATCH_PARTICIPANT);
        Activity activity3 = Activity.of(a, ContentCategory.MEAL, MATCH_ABSENT);
        Activity activity4 = Activity.of(a, ContentCategory.STUDY, MATCH_REVIEW_1);

        Activity activity5 = Activity.of(b, ContentCategory.STUDY, MATCH_REVIEW_2);

        activityRepository.saveAll(List.of(activity1, activity2, activity3, activity4, activity5));
        //when
        ActivitySearch activitySearchMeal = new ActivitySearch();
        activitySearchMeal.setContentCategory(ContentCategory.MEAL);
        List<ActivityMatchScore> activityMatchScoreByMeal = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
            a.getId(), activitySearchMeal);

        ActivitySearch activitySearchStudy = new ActivitySearch();
        activitySearchStudy.setContentCategory(ContentCategory.STUDY);
        List<ActivityMatchScore> activityMatchScoreByStudy = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
            a.getId(), activitySearchStudy);

        ActivitySearch activitySearchStudyAndB = new ActivitySearch();
        activitySearchStudyAndB.setContentCategory(ContentCategory.STUDY);
        List<ActivityMatchScore> activityMatchScoreByStudyAndB = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
            b.getId(), activitySearchStudyAndB);

        //then
        assertThat(activityMatchScoreByMeal).containsOnly(ARTICLE_MATCH_AUTHOR, MATCH_PARTICIPANT,
            MATCH_ABSENT);

        assertThat(activityMatchScoreByStudy).containsOnly(MATCH_REVIEW_1);
        assertThat(activityMatchScoreByStudyAndB).containsOnly(MATCH_REVIEW_2);
    }


    @Test
    void findActivityMatchScoreByMemberIdAndArticleSearch_givenActivities_whenActivitySearchStartTime_thenContainsOnly() {
        //given
        Member a = Member.of("a");
        Member b = Member.of("b");
        memberRepository.saveAll(List.of(a, b));
        Activity activity1 = Activity.of(a, ContentCategory.MEAL, ARTICLE_MATCH_AUTHOR);
        Activity activity2 = Activity.of(a, ContentCategory.MEAL, MATCH_PARTICIPANT);
        Activity activity3 = Activity.of(a, ContentCategory.MEAL, MATCH_ABSENT);
        Activity activity4 = Activity.of(a, ContentCategory.STUDY, MATCH_REVIEW_1);

        Activity activity5 = Activity.of(b, ContentCategory.STUDY, MATCH_REVIEW_2);

        activityRepository.saveAll(List.of(activity1, activity2, activity3, activity4, activity5));
        //when
        ActivitySearch activitySearchStartTimeBeforeEndTime = new ActivitySearch();
        activitySearchStartTimeBeforeEndTime.setStartTime(LocalDateTime.now().minusDays(1));
        List<ActivityMatchScore> activityMatchFindByStartTimeBeforeEndtime = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
            a.getId(), activitySearchStartTimeBeforeEndTime);

        ActivitySearch activitySearchStartTimeAfterEndTime = new ActivitySearch();
        activitySearchStartTimeAfterEndTime.setStartTime(LocalDateTime.now().plusDays(1));
        List<ActivityMatchScore> activityMatchFindByStartTimeAfterEndtime = activityRepository.findActivityMatchScoreByMemberIdAndArticleSearch(
            a.getId(), activitySearchStartTimeAfterEndTime);

        //then
        assertThat(activityMatchFindByStartTimeBeforeEndtime).hasSize(4);
        assertThat(activityMatchFindByStartTimeAfterEndtime).isEmpty();
    }

}