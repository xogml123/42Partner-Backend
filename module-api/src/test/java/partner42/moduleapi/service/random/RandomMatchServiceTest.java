package partner42.moduleapi.service.random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import partner42.modulecommon.utils.redis.RedisTransactionUtil;


@SpringBootTest()
class RandomMatchServiceTest {

    @Autowired
    private RedisTransactionUtil redisTransactionUtil;
    @Autowired
    private RandomMatchRedisRepository randomMatchRedisRepository;
    @Test
    void redis() {
        //given
        int a = 2;
        redisTransactionUtil.wrapTransaction(() -> {
            randomMatchRedisRepository.addToSet("test1", "a");
            if (a == 3){
                throw new RuntimeException();
            }
            randomMatchRedisRepository.addToSet("test2", "a");


        });
        //when

        //then
    }
}