package partner42.moduleapi.service.article;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.moduleapi.config.kafka.KafkaConsumerConfig;
import partner42.moduleapi.config.kafka.KafkaProducerConfig;
import partner42.moduleapi.config.kafka.KafkaTopicConfig;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.testutils.WorkerWithCountDownLatch;
import partner42.modulecommon.config.redis.LettuceConnectionConfig;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.articlemember.ArticleMemberRepository;
import partner42.modulecommon.repository.member.MemberRepository;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestBootstrapConfig.class})
@Slf4j
class ArticleServiceOptimisticLockingWithDaoTest {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleMemberRepository articleMemberRepository;

    /**
     * 낙관적 락 AOP처리 제대로 동작하는지 확인
     */
    @Test
    void participateArticle_whenMultiTransactionConcurrentlyStart_thenOptimisticLockExceptionOrDeadLockExceptionOccurAndRetryApplied() throws Exception{
        //given
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ArticleDto articleDto = ArticleDto.builder()
            .anonymity(false)
            .content("content")
            .title("title")
            .date(tomorrow)
            .contentCategory(ContentCategory.MEAL)
            .participantNumMax(3)
            .matchConditionDto(MatchConditionDto.builder()
                .placeList(new ArrayList<>())
                .timeOfEatingList(new ArrayList<>())
                .wayOfEatingList(new ArrayList<>())
                .build())
            .build();

        ArticleOnlyIdResponse articleOnlyIdResponse = articleService.createArticle("takim",
            articleDto);

        Thread.sleep(100);
        //when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //동시에 두 요청이 들어왔을 때 retry를 안정적으로 잘 해주는지 검증.
        WorkerWithCountDownLatch sorkimParticipate = new WorkerWithCountDownLatch(
            "sorkim participate", countDownLatch, () ->
        {

            articleService.participateArticle("sorkim",
                articleOnlyIdResponse.getArticleId());
        });

        WorkerWithCountDownLatch hyenamParticipate = new WorkerWithCountDownLatch(
            "hyenam participate", countDownLatch, () ->
        {
            articleService.participateArticle("hyenam",
                articleOnlyIdResponse.getArticleId());
        });
        sorkimParticipate.start();
        hyenamParticipate.start();

        Thread.sleep(10);
        log.info("-----------------------------------------------");
        log.info(" Now release the latch:");
        log.info("-----------------------------------------------");
        countDownLatch.countDown();
        Thread.sleep(2000);
        //then

        //LostUpdate 발생 하지않는지
        assertThat(
            articleRepository.findByApiIdAndIsDeletedIsFalse(articleOnlyIdResponse.getArticleId()).get()
                .getParticipantNum()).isEqualTo(3);
        assertThat(
            articleMemberRepository.findByArticleApiId(articleOnlyIdResponse.getArticleId())
        ).hasSize(3);
    }

}