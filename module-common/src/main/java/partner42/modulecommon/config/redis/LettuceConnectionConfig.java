package partner42.modulecommon.config.redis;


import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class LettuceConnectionConfig {


    @Value("${spring.redis.host}")
    String masterHost;

    @Value("${redis.replica.host}")
    String redisReplica;

    @Value("${redis.replica.port}")
    int redisReplicaPort;

    @Value("${spring.redis.port}")
    int port;

    @Value("${spring.redis.password}")
    String password;


    @Value("${spring.redis.ssl}")
    boolean useSSL;

    @Value("${redis.expire.default}")
    private long defaultExpireSecond;

//    private final EntityManagerFactory entityManagerFactory;
//    private final DataSource dataSource;

    /*
     * Class <=> Json간 변환을 담당한다.
     *
     * json => object 변환시 readValue(File file, T.class) => json File을 읽어 T 클래스로 변환 readValue(Url url,
     * T.class) => url로 접속하여 데이터를 읽어와 T 클래스로 변환 readValue(String string, T.class) => string형식의
     * json데이터를 T 클래스로 변환
     *
     * object => json 변환시 writeValue(File file, T object) => object를 json file로 변환하여 저장
     * writeValueAsBytes(T object) => byte[] 형태로 object를 저장 writeValueAsString(T object) => string 형태로
     * object를 json형태로 저장
     *
     * json을 포매팅(개행 및 정렬) writerWithDefaultPrettyPrint().writeValueAs... 를 사용하면 json파일이 포맷팅하여 저장된다.
     * object mapper로 date값 변환시 timestamp형식이 아니라 JavaTimeModule() 로 변환하여 저장한다.
     */

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module());
        return mapper;
    }


    /**
     * Redis readReplica를 추가하기 위한 설정
     * @return
     */
    @Bean
    public RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration() {
        RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration =
            new RedisStaticMasterReplicaConfiguration(masterHost, port);
        redisStaticMasterReplicaConfiguration.addNode(redisReplica, redisReplicaPort);
        redisStaticMasterReplicaConfiguration.setPassword(password);
        return redisStaticMasterReplicaConfiguration;
    }

    /*
     * Redis Connection Factory library별 특징
     * 1. Jedis - 멀티쓰레드환경에서 쓰레드 안전을 보장하지 않는다.
     *          - Connection pool을 사용하여 성능, 안정성 개선이 가능하지만 Lettuce보다 상대적으로 하드웨어적인 자원이 많이 필요하다.
     *          - 비동기 기능을 제공하지 않는다.
     *
     * 2. Lettuce - Netty 기반 redis client library
     *            - 비동기로 요청하기 때문에 Jedis에 비해 높은 성능을 가지고 있다.
     *            - TPS, 자원사용량 모두 Jedis에 비해 우수한 성능을 보인다는 테스트 사례가 있다.
     *
     * Jedis와 Lettuce의 성능 비교  https://jojoldu.tistory.com/418
     */

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
        final RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration) {
        final SocketOptions socketOptions =
            SocketOptions.builder().connectTimeout(Duration.of(10, ChronoUnit.MINUTES)).build();

        final var clientOptions =
            ClientOptions.builder().socketOptions(socketOptions).autoReconnect(true).build();

        var clientConfig =
            LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .readFrom(REPLICA_PREFERRED);
        if (useSSL) {
            // aws elasticcache uses in-transit encryption therefore no need for providing certificates
            clientConfig = clientConfig.useSsl().disablePeerVerification().and();
        }

        return new LettuceConnectionFactory(
            redisStaticMasterReplicaConfiguration, clientConfig.build());
    }


//    @Bean // 만약 PlatformTransactionManager 등록이 안되어 있다면 해야함, 되어있다면 할 필요 없음
//    public PlatformTransactionManager transactionManager() throws SQLException {
//        // 사용하고 있는 datasource 관련 내용, 아래는 JDBC
////        return new DataSourceTransactionManager(dataSource);
//
//        // JPA 사용하고 있다면 아래처럼 사용하고 있음
//        return new JpaTransactionManager(entityManagerFactory);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(ObjectMapper objectMapper) {
        GenericJackson2JsonRedisSerializer serializer =
            new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory(redisStaticMasterReplicaConfiguration()));
        // json 형식으로 데이터를 받을 때
        // 값이 깨지지 않도록 직렬화한다.
        // 저장할 클래스가 여러개일 경우 범용 JacksonSerializer인 GenericJackson2JsonRedisSerializer를 이용한다
        // 참고 https://somoly.tistory.com/134
        // setKeySerializer, setValueSerializer 설정해주는 이유는 RedisTemplate를 사용할 때 Spring - Redis 간 데이터 직렬화, 역직렬화 시 사용하는 방식이 Jdk 직렬화 방식이기 때문입니다.
        // 동작에는 문제가 없지만 redis-cli을 통해 직접 데이터를 보려고 할 때 알아볼 수 없는 형태로 출력되기 때문에 적용한 설정입니다.
        // 참고 https://wildeveloperetrain.tistory.com/32
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setEnableTransactionSupport(true); // transaction 허용

        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory(redisStaticMasterReplicaConfiguration()));
        return container;
    }

//
//    /**
//     * Redis Cache를 사용하기 위한 cache manager 등록.<br>
//     * 커스텀 설정을 적용하기 위해 RedisCacheConfiguration을 먼저 생성한다.<br>
//     * 이후 RadisCacheManager를 생성할 때 cacheDefaults의 인자로 configuration을 주면 해당 설정이 적용된다.<br>
//     * RedisCacheConfiguration 설정<br>
//     * disableCachingNullValues - null값이 캐싱될 수 없도록 설정한다. null값 캐싱이 시도될 경우 에러를 발생시킨다.<br>
//     * entryTtl - 캐시의 TTL(Time To Live)를 설정한다. Duraction class로 설정할 수 있다.<br>
//     * serializeKeysWith - 캐시 Key를 직렬화-역직렬화 하는데 사용하는 Pair를 지정한다.<br>
//     * serializeValuesWith - 캐시 Value를 직렬화-역직렬화 하는데 사용하는 Pair를 지정한다.
//     * Value는 다양한 자료구조가 올 수 있기 때문에 GenericJackson2JsonRedisSerializer를 사용한다.
//     *
//     * @param redisConnectionFactory Redis와의 연결을 담당한다.
//     * @return
//     */
//    @Bean
//    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
//        ObjectMapper objectMapper) {
//        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
//            .disableCachingNullValues()
//            .entryTtl(Duration.ofSeconds(defaultExpireSecond))
//            .serializeKeysWith(
//                RedisSerializationContext.SerializationPair
//                    .fromSerializer(new StringRedisSerializer()))
//            .serializeValuesWith(
//                RedisSerializationContext.SerializationPair
//                    .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
//
//        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
//            .cacheDefaults(configuration).build();
//    }


}