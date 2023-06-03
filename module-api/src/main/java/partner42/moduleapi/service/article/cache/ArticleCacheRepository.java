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

@RequiredArgsConstructor
public class ArticleCacheRepository {
    private static final int TTL = 60;
    private static final double BETA = 1.0;
    private static final String SET_SCRIPT = "redis.call('mset', KEYS[1], ARGV[1], KEYS[2], ARGV[2]);" +
        "redis.call('expire', KEYS[1], ARGV[3]);" +
        "redis.call('expire', KEYS[2], ARGV[3])";
    private static final String GET_SCRIPT = "return {redis.call('mget', KEYS[1], KEYS[2])," +
        "redis.call('ttl', KEYS[1])}";

    private final Random random;
    private final RedisTemplate<String, Object> redisTemplate;

    public String probabilisticEarlyRecomputationGet(String key, Function<String, String> recomputer) {

        List<Object> ret = (List<Object>) jedisCluster.eval(GET_SCRIPT, 2, key, getDeltaKey(key));
        List<String> valueList = (List<String>) ret.get(0);
        String data = valueList.get(0);
        String deltaStr = valueList.get(1);
        new Random()
        if (data == null ||
            - Long.valueOf(deltaStr) * BETA * Math.log(random.nextDouble()) >= (long) ret.get(1)) {
            long start = System.currentTimeMillis();
            data = recomputer.apply(key);
            long computationTime = (System.currentTimeMillis() - start) * 1000;
            jedisCluster.eval(SET_SCRIPT, 2, key, getDeltaKey(key), data, computationTime, ttl);
        }
        return data;
    }

    private String getDeltaKey(String key) {
        return key;
    }
}
