package partner42.moduleapi.config.cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import partner42.moduleapi.dto.article.ArticleReadResponse;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Integer DEFAULT_EXPIRE_SECOND = 60 * 5;
    private static final String ARTICLES_CACHE_NAME = "articles";

    private static final Integer ARTICLES_EXPIRE_SECOND = 5;


    /**
     * Redis Cache를 사용하기 위한 cache manager 등록.<br>
     * 커스텀 설정을 적용하기 위해 RedisCacheConfiguration을 먼저 생성한다.<br>
     * 이후 RadisCacheManager를 생성할 때 cacheDefaults의 인자로 configuration을 주면 해당 설정이 적용된다.<br>
     * RedisCacheConfiguration 설정<br>
     * disableCachingNullValues - null값이 캐싱될 수 없도록 설정한다. null값 캐싱이 시도될 경우 에러를 발생시킨다.<br>
     * entryTtl - 캐시의 TTL(Time To Live)를 설정한다. Duraction class로 설정할 수 있다.<br>
     * serializeKeysWith - 캐시 Key를 직렬화-역직렬화 하는데 사용하는 Pair를 지정한다.<br>
     * serializeValuesWith - 캐시 Value를 직렬화-역직렬화 하는데 사용하는 Pair를 지정한다. -> 가시성이 중요하지 않기 때문에 JdkSerializationRedisSerializer 사용<br>
     * Value는 다양한 자료구조가 올 수 있기 때문에 GenericJackson2JsonRedisSerializer를 사용한다.
     *
     * @param redisConnectionFactory Redis와의 연결을 담당한다.
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
        ObjectMapper objectMapper) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofSeconds(DEFAULT_EXPIRE_SECOND))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new StringRedisSerializer()));
//            .serializeValuesWith(
//                RedisSerializationContext.SerializationPair
//                    .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
        
//        JavaType type = objectMapper.getTypeFactory().constructParametricType(SliceImpl.class, ArticleReadResponse.class);
//        Jackson2JsonRedisSerializer<SliceImpl<ArticleReadResponse>> serializer = new Jackson2JsonRedisSerializer<>(type);
//        serializer.setObjectMapper(objectMapper);
//        HashMap<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//        cacheConfigurations.put(ARTICLES_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig()
//            .disableCachingNullValues()
//            .entryTtl(Duration.ofSeconds(ARTICLES_EXPIRE_SECOND))
//            .serializeKeysWith(
//                RedisSerializationContext.SerializationPair
//                    .fromSerializer(new StringRedisSerializer()))
//            .serializeValuesWith(
//                RedisSerializationContext.SerializationPair
//                    .fromSerializer(serializer)));

        HashMap<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(ARTICLES_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofSeconds(ARTICLES_EXPIRE_SECOND))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair
                    .fromSerializer(new StringRedisSerializer())));


        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .cacheDefaults(configuration)
//            .transactionAware()
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();

    }
}
