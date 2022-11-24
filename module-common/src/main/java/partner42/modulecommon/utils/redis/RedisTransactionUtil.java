package partner42.modulecommon.utils.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTransactionUtil {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * redis transaction을 실행함.
     * command성 메소드를 주로 실행.
     * @param function
     */
    public void wrapTransaction(Supplier<Object> function){
        redisTemplate.execute(new SessionCallback() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi(); // redis transaction 시작

                function.get();
                return operations.exec(); // redis transaction 종료
            }
        });
    }

    /**
     * redis transaction을 실행함.
     * command성 메소드를 주로 실행.
     * @param function
     */
    public void wrapTransaction(Runnable function){
        redisTemplate.execute(new SessionCallback() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi(); // redis transaction 시작

                function.run();
                return operations.exec(); // redis transaction 종료
            }
        });
    }
}
