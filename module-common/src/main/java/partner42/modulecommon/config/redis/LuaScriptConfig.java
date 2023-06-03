package partner42.modulecommon.config.redis;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class LuaScriptConfig {

    @Bean
    public DefaultRedisScript<List<Object>> cacheGetRedisScript(){
        DefaultRedisScript<List<Object>> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("cache_get.lua")));
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<Object> cacheSetRedisScript(){
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("cache_set.lua")));
        redisScript.setResultType(null);
        return redisScript;
    }
}
