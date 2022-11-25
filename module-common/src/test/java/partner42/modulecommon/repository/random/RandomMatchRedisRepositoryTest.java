package partner42.modulecommon.repository.random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import partner42.modulecommon.utils.redis.RedisTransactionUtil;

@SpringBootTest
class RandomMatchRedisRepositoryTest {

    @Autowired
    private RedisTransactionUtil redisTransactionUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void tdd(){
        //given

        //when

        //then
    }
}