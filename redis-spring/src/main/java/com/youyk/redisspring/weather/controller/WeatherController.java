package com.youyk.redisspring.weather.controller;

import com.youyk.redisspring.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("weather")
public class WeatherController {
    private final WeatherService service;

    @GetMapping("{zip}")
    public Mono<Integer> getWeather(@PathVariable("zip") int zip){
        return Mono.fromSupplier(() -> this.service.getInfo(zip));
    }
}
