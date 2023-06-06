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
    public DefaultRedisScript<List> cacheGetRedisScript(){
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("luascript/cache_get.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    public DefaultRedisScript<List> cacheSetRedisScript(){
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("luascript/cache_set.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }
}
