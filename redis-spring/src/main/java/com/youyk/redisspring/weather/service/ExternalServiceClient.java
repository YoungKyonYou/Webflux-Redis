package com.youyk.redisspring.weather.service;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class ExternalServiceClient {
    /**
     * @Cacheable과는 달리, @CachePut은 메소드가 호출될 때마다 캐시를 업데이트합니다.
     * 즉, 메소드가 호출될 때마다 메소드는 항상 실행되고 그 결과가 캐시에 저장됩니다.
     * @CachePut은 주로 캐시를 최신 상태로 유지해야 하는 경우에 사용됩니다.
     */
    @CachePut(value = "weather", key = "#zip")
    public int getWeatherInfo(int zip){
        return ThreadLocalRandom.current().nextInt(60, 100);
    }
}
