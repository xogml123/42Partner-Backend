package partner42.moduleapi.service.article.cache;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import partner42.moduleapi.config.cache.ProbabilisticEarlyRecomputationConfig.RandomDoubleGenerator;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleCacheService {
    private static final double BETA = 1.0;
    private static final String DELTA = "delta";

    private final RandomDoubleGenerator randomDoubleGenerator;

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<List> cacheGetRedisScript;
    private final DefaultRedisScript<List> cacheSetRedisScript;
    public Object probabilisticEarlyRecomputationGet(String originKey, Function<List<Object>, Object> recomputer, List<Object> args, Integer ttl) {
        String key = hashtags(originKey);
        List<Object> ret = (List<Object>)redisTemplate.execute(cacheGetRedisScript, List.of(key, getDeltaKey(key)));
        List<Object> valueList = (List<Object>) ret.get(0);
        Object data = valueList.get(0);
        Long delta = (Long)valueList.get(1);
        Long remainTtl = (Long)ret.get(1);
        log.debug("data: {}, delta: {}, remainTtl: {}", data, delta, remainTtl);
        // 재 갱신을 해야하는 경우.
        if (data == null || delta == null || remainTtl == null ||
            - delta * BETA * Math.log(randomDoubleGenerator.nextDouble()) >= remainTtl) {
            long start = System.currentTimeMillis();
            data = recomputer.apply(args);
            long computationTime = (System.currentTimeMillis() - start);
            redisTemplate.execute(cacheSetRedisScript, List.of(key, getDeltaKey(key)), data, computationTime, ttl);
            setKeyAndDeltaWithPipeline(ttl, key, data, computationTime);
        }
        return data;
    }

    private void setKeyAndDeltaWithPipeline(Integer ttl, String key, Object data, long computationTime) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] keyBytes = serializer.serialize(key);
            byte[] deltaKeyBytes = serializer.serialize(getDeltaKey(key));
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
            byte[] dataBytes = valueSerializer.serialize(data);
            byte[] computationTimeBytes = valueSerializer.serialize(computationTime);

            connection.set(keyBytes, dataBytes);
            connection.set(deltaKeyBytes, computationTimeBytes);

            long ttlLong = Long.parseLong(ttl.toString());
            Duration duration = Duration.of(ttlLong, ChronoUnit.SECONDS);
            connection.expire(keyBytes, duration.getSeconds());
            connection.expire(deltaKeyBytes, duration.getSeconds());

            return null;
        });
    }

    private String hashtags(String originKey) {
        return "{" + originKey+ "}";
    }

    private String getDeltaKey(String key) {
        return key + "-" + DELTA;

    }
}
