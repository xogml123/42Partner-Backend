package partner42.moduleapi.service.article.cache;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import partner42.moduleapi.config.cache.ProbabilisticEarlyRecomputationConfig;
import partner42.modulecommon.config.redis.LettuceConnectionConfig;
import partner42.modulecommon.config.redis.LuaScriptConfig;
import partner42.modulecommon.subscriber.RedisMessageSubscriber;


@Slf4j
@SpringBootTest(classes = {ArticleCacheService.class, LettuceConnectionConfig.class,
    LuaScriptConfig.class})
class ArticleCacheServiceTest {

    @MockBean
    private ProbabilisticEarlyRecomputationConfig.RandomDoubleGenerator randomDoubleGenerator;

    @MockBean
    private RedisMessageSubscriber redisMessageSubscriber;

    @Autowired
    private ArticleCacheService articleCacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void probabilisticEarlyRecomputationGet_whenCacheDataIsAbsent_thenComputeAndWrite() {
        //given
        String key = "target1";
//        when(randomDoubleGenerator.nextDouble()).thenReturn(0.5);
        //when
        articleCacheService.probabilisticEarlyRecomputationGet(key, (x) -> {
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "a";
        }, List.of(), 5);
        String value = (String) redisTemplate.opsForValue().get(hashtag(key));
        Object delta = redisTemplate.opsForValue().get(getDelta(hashtag(key)));

        //then
        assertThat(value)
            .isEqualTo("a");
        assertThat(delta)
            .isNotNull();
    }

    @Test
    void probabilisticEarlyRecomputationGet_givenCacheDataPresent_whenRemaintTTLIsMuchLargerThanDelta_thenNotRecompute() {
        //given
        String key = "target1";
        String hashtag = hashtag(key);
        redisTemplate.opsForValue().set(hashtag, "a");
        redisTemplate.opsForValue().set(getDelta(hashtag), 100l);
        Duration duration = Duration.of(5, ChronoUnit.SECONDS);
        redisTemplate.expire(hashtag, duration);
        redisTemplate.expire(getDelta(hashtag), duration);

        when(randomDoubleGenerator.nextDouble()).thenReturn(0.1);
        //when
        ArrayList<Object> args = new ArrayList<>();
        ArrayList<String> check = new ArrayList<>();
        args.add(check);
        // 100 * betta log(0.1) >= 5000
        articleCacheService.probabilisticEarlyRecomputationGet(key, (x) -> {
            ArrayList<String> check1 = (ArrayList<String>)(x.get(0));
            check1.add("3");
            return "a";
        }, args, 5);
        //실행 되지 않았는지 확인.
        assertThat(check).isEmpty();
    }

    @Test
    void probabilisticEarlyRecomputationGet_givenCacheDataPresent_whenRemaintTTLIsSmallerThanDelta_thenRecompute() {
        //given
        String key = "target1";
        String hashtag = hashtag(key);
        redisTemplate.opsForValue().set(hashtag, "a");
        redisTemplate.opsForValue().set(getDelta(hashtag), 5005l);
        Duration duration = Duration.of(5, ChronoUnit.SECONDS);
        redisTemplate.expire(hashtag, duration);
        redisTemplate.expire(getDelta(hashtag), duration);

        when(randomDoubleGenerator.nextDouble()).thenReturn(0.1);
        //when
        ArrayList<Object> args = new ArrayList<>();
        ArrayList<String> check = new ArrayList<>();
        args.add(check);
        // 100 * betta log(0.1) >= 5000
        articleCacheService.probabilisticEarlyRecomputationGet(key, (x) -> {
            ArrayList<String> check1 = (ArrayList<String>)(x.get(0));
            check1.add("3");
            return "a";
        }, args, 5);
        //실행 되었는지 않았는지 확인.
        assertThat(check).isNotEmpty();
    }

    private String hashtag(String key) {
        return "{" + key + "}";
    }

    private String getDelta(String key) {
        return key + "-" + "delta";
    }
}