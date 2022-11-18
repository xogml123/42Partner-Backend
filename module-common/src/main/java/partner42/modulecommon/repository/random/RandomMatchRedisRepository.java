package partner42.modulecommon.repository.random;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RandomMatchRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public void addToSortedSet(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public void addToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void deleteSortedSet(String key, String value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    public void deleteAllSortedSet(String key, Object[] value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    public void deleteSet(String key, Object[] value) {
        redisTemplate.opsForSet().remove(key, value);
    }
}
