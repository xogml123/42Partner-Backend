package partner42.moduleapi.service.random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import partner42.modulecommon.repository.random.RandomMatchRedisRepository;

@SpringBootTest
class RandomMatchServiceTest {

    @Autowired
    private RandomMatchRedisRepository randomMatchRedisRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Test
    void redisTransactionalWork(){
        randomMatchRedisRepository.redisTransactionalWork("abc");
    }
}