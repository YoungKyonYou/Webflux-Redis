package com.youyk.redisspring.fib.config;


import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonCacheConfig {
    /**
     * RedissonSpringCacheManager를 사용하여 CacheManager를 구현하고 있습니다.
     * RedissonSpringCacheManager는 Redisson 라이브러리를 사용하여 Redis를 캐시 저장소로 사용할 수 있게 해줍니다.
     * RedissonClient는 Redisson 라이브러리의 주요 인터페이스로, Redis 서버와의 연결을 관리
     * 이를 RedissonSpringCacheManager의 생성자에 전달함으로써, CacheManager가 Redis 서버와 통신할 수 있게 됩니다.
     * Spring의 @Cacheable, @CacheEvict 등의 캐시 관련 어노테이션을 사용하여 캐시를 쉽게 관리할 수 있게 됩니다.
     */
    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient){
        return new RedissonSpringCacheManager(redissonClient);
    }
}
