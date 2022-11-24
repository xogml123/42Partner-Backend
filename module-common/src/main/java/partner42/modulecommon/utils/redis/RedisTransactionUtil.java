package partner42.modulecommon.utils.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
    public void wrapTransaction(Function<Object, Object> function){
        redisTemplate.execute(new SessionCallback() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi(); // redis transaction 시작

                function.apply(null);
                return operations.exec(); // redis transaction 종료
            }
        });
    }
}
