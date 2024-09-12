package com.youyk.redisspring.city.controller;

import com.youyk.redisspring.city.dto.City;
import com.youyk.redisspring.city.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("city")
public class CityController {
    private final CityService cityService;

    @GetMapping("{zipCode}")
    public Mono<City> getCity(@PathVariable("zipCode") final String zipCode){
        return this.cityService.getCity(zipCode);
    }
}
