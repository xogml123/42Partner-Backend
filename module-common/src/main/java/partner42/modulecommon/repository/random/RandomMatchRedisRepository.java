//package partner42.modulecommon.repository.random;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.stream.Collectors;
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.redis.core.RedisOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.SessionCallback;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//@RequiredArgsConstructor
//@Repository
//public class RandomMatchRedisRepository {
//
//    private final RedisTemplate<String, String> redisTemplate;
//
//    public List<String> getAllSortedSet(String key) {
//        return redisTemplate.opsForZSet().range(key, 0, -1).stream()
//            .collect(Collectors.toList());
//    }
//
//    public void addToSortedSet(String key, String value, double score) {
//        redisTemplate.opsForZSet().add(key, value, score);
//    }
//    public void deleteSortedSet(String key, String value) {
//        redisTemplate.opsForZSet().remove(key, value);
//    }
//    public void deleteAllSortedSet(String key, Object[] value) {
//        redisTemplate.opsForZSet().remove(key, value);
//    }
//
//    public void addToSet(String key, String value) {
//        redisTemplate.opsForSet().add(key, value);
//    }
//
//
//
//    public void deleteSet(String key, Object[] value) {
//        redisTemplate.opsForSet().remove(key, value);
//    }
//
//
//}
