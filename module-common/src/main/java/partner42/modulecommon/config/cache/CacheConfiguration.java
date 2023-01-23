//package partner42.modulecommon.config.cache;
//
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.ehcache.EhCacheCacheManager;
//import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//
//@EnableCaching
//@Configuration
//public class CacheConfiguration {
//
//    @Bean
//    public CacheManager cacheManager() {
//        return new EhCacheCacheManager(cacheManagerFactory().getObject());
//
//    }
//    @Bean
//    public EhCacheManagerFactoryBean cacheManagerFactory() {
//        EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
//        factory.setConfigLocation(new ClassPathResource("ehcache.xml"));
//        factory.setShared(true);
//        return factory;
//    }
//}
