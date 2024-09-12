package com.youyk.redisspring.weather.service;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WeatherService {
    private final ExternalServiceClient client;

    @Cacheable("weather")
    public int getInfo(int zip){
        return 0;
    }

    @Scheduled(fixedRate = 10_000)
    public void update(){
        System.out.println("updating weather");
        IntStream.rangeClosed(1, 5)
                .forEach(this.client::getWeatherInfo);
    }

}
