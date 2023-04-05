package partner42.moduleapi.service.activity;


import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.config.ServiceWithDAOTestDefaultConfig;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.moduleapi.dto.activity.ActivityScoreResponse;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.activity.ActivityRepository;
import partner42.modulecommon.repository.activity.ActivitySearch;
import partner42.modulecommon.repository.user.UserRepository;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    ActivityService.class,
    ServiceWithDAOTestDefaultConfig.class,
})
class ActivityServiceWithDAOTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityRepository activityRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void readMyActivityScoreSum_() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        Activity a1 = Activity.of(takim.getMember(), ContentCategory.MEAL,
            ActivityMatchScore.ARTICLE_MATCH_AUTHOR);
        Activity a2 = Activity.of(takim.getMember(), ContentCategory.MEAL,
            ActivityMatchScore.MATCH_REVIEW_1);
        Activity a3 = Activity.of(takim.getMember(), ContentCategory.STUDY,
            ActivityMatchScore.ARTICLE_MATCH_AUTHOR);

        Activity a4 = Activity.of(sorkim.getMember(), ContentCategory.MEAL,
            ActivityMatchScore.ARTICLE_MATCH_AUTHOR);
        //when
        activityRepository.saveAll(List.of(a1, a2, a3, a4));

        ActivitySearch asMeal = new ActivitySearch();
        asMeal.setContentCategory(ContentCategory.MEAL);
        ActivitySearch asStudy = new ActivitySearch();
        asStudy.setContentCategory(ContentCategory.STUDY);

        ActivityScoreResponse asrTakimMeal = activityService.readMyActivityScoreSum(
            takim.getUsername(), asMeal);
        ActivityScoreResponse asrTakimStudy = activityService.readMyActivityScoreSum(
            takim.getUsername(), asStudy);
        ActivityScoreResponse asrSorkimMeal = activityService.readMyActivityScoreSum(
            sorkim.getUsername(), asMeal);
        ActivityScoreResponse asrSorkimStudy = activityService.readMyActivityScoreSum(
            sorkim.getUsername(), asStudy);
        //then
        assertThat(asrTakimMeal.getScore()).isEqualTo(
            ActivityMatchScore.ARTICLE_MATCH_AUTHOR.getScore()
                + ActivityMatchScore.MATCH_REVIEW_1.getScore());
        assertThat(asrTakimStudy.getScore()).isEqualTo(
            ActivityMatchScore.ARTICLE_MATCH_AUTHOR.getScore());
        assertThat(asrSorkimMeal.getScore()).isEqualTo(
            ActivityMatchScore.ARTICLE_MATCH_AUTHOR.getScore());
        assertThat(asrSorkimStudy.getScore()).isEqualTo(
            0);
    }
}