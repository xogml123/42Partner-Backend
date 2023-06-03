package partner42.moduleapi.service.article.cache;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleCacheRepository {
    private static final int TTL = 5;
    private static final double BETA = 1.0;
    private static final String DELTA = "delta";

    private final Random random;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<List<Object>> cacheGetRedisScript;
    private final DefaultRedisScript<Object> cacheSetRedisScript;
    public Object probabilisticEarlyRecomputationGet(String key, Function<List<Object>, Object> recomputer, List<Object> args) {
        List<Object> ret = redisTemplate.execute(cacheGetRedisScript, List.of(key, getDeltaKey(key)));
        List<String> valueList = (List<String>) ret.get(0);
        Object data = valueList.get(0);
        String deltaStr = valueList.get(1);
        // 재 갱신을 해야하는 경우.
        if (data == null ||
            - Long.valueOf(deltaStr) * BETA * Math.log(random.nextDouble()) >= (long) ret.get(1)) {
            long start = System.currentTimeMillis();
            data = recomputer.apply(args);
            long computationTime = (System.currentTimeMillis() - start) * 1000;
            redisTemplate.execute(cacheSetRedisScript, List.of(key, getDeltaKey(key)), data, computationTime, TTL);
        }
        return data;
    }

    private String getDeltaKey(String key) {
        return key + "-" + DELTA;
    }
}
