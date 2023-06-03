package partner42.modulecommon.config.redis;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class LuaScriptConfig {

    @Bean
    public RedisScript<List<Object>> redisScript(){
        DefaultRedisScript<List<Object>> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("cache_get.lua")));
        return redisScript;
    }
}
