package partner42.moduleapi.service.article;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.utils.CreateTestDataUtils;
import partner42.moduleapi.utils.WorkerWithCountDownLatch;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.member.MemberRepository;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CreateTestDataUtils createTestDataUtils;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * 낙관적 락 AOP처리 제대로 동작하는지 확인
     */
    @Test
    void participateArticle() throws Exception{
        //given
        createTestDataUtils.signUpUsers();
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

        ArticleOnlyIdResponse articleOnlyIdResponse = articleService.createArticle("takim@student.42Seoul.kr",
            articleDto);
        //when
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //동시에 두 요청이 들어왔을 때 retry를 안정적으로 잘 해주는지 검증.
        WorkerWithCountDownLatch sorkimParticipate = new WorkerWithCountDownLatch(
            "sorkim participate", countDownLatch, () ->
        {

            articleService.participateArticle("sorkim@student.42Seoul.kr",
                articleOnlyIdResponse.getArticleId());
        });

        WorkerWithCountDownLatch hyenamParticipate = new WorkerWithCountDownLatch(
            "hyenam participate", countDownLatch, () ->
        {
            articleService.participateArticle("hyenam@student.42Seoul.kr",
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
        assertThat(
            articleRepository.findByApiIdAndIsDeletedIsFalse(articleOnlyIdResponse.getArticleId()).get()
                .getParticipantNum()).isEqualTo(3);

    }

}