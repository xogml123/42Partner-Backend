package partner42.moduleapi.service.article;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleReadResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.service.user.UserService;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.article.ArticleSearch;
import partner42.modulecommon.repository.user.UserRepository;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestBootstrapConfig.class})
@Slf4j
class ArticleServiceRedisCacheTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void readAllArticle() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        for (int i = 0; i < 100; i++) {
            articleService.createArticle(takim.getUsername(),
                ArticleDto.builder()
                    .anonymity(false)
                    .content(Integer.toString(i))
                    .contentCategory(ContentCategory.MEAL)
                    .participantNumMax(4)
                    .date(LocalDate.now().plusDays(1L))
                    .title("title")
                    .matchConditionDto(MatchConditionDto.builder()
                        .typeOfStudyList(List.of())
                        .timeOfEatingList(List.of(TimeOfEating.DINNER))
                        .placeList(List.of())
                        .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                        .build())
                    .build());
        }
        PageRequest page = PageRequest.of(0, 20, Sort.by(Order.asc("createdAt")));
        ArticleSearch articleSearch = new ArticleSearch();
        articleSearch.setContentCategory(ContentCategory.MEAL);
        //when
        articleService.readAllArticle(page, articleSearch);
        //then
        SliceImpl<ArticleReadResponse> result = articleService.readAllArticle(page,
            articleSearch);


    }
}