package partner42.moduleapi.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfiguration {


    @Value("${spring.redis.port}")
    private int port;
    private RedisServer redisServer;

    public TestRedisConfiguration() {
        try{
            this.redisServer = new RedisServer(port);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
