package com.youyk.redisspring.city.service;

import com.youyk.redisspring.city.client.CityClient;
import com.youyk.redisspring.city.dto.City;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class CityService {


    private CityClient cityClient;

    //RMapReactive<String, City>는 Redisson의 비동기적인 Redis 맵 객체
    //Redis에서 key-value 형식으로 데이터를 저장하고 읽어오는 데 사용
    //RMapCacheReactive는 개별 맵 항목에 대한 TTL(Time To Live)을 설정할 수 있는 RMapReactive의 확장입니다.
/*    private RMapCacheReactive<String, City> cityMap;

    public CityService(RedissonReactiveClient client, CityClient cityClient) {
        //client.getMap("city", new TypedJsonJacksonCodec(String.class, City.class))은 Redis의 특정 맵을 가져오는 명령입니다.
        //TypedJsonJacksonCodec(String.class, City.class)는 Redis에 저장할 데이터를 (JSON 형식으로) 직렬화 및 역직렬화할 때 사용할 타입 정보를 제공합니다.
        //String.class: 맵의 키 타입.
        //City.class: 맵의 값 타입, 즉 도시 정보를 담고 있는 객체 타입.
        this.cityMap = client.getMapCache("city", new TypedJsonJacksonCodec(String.class, City.class));
        this.cityClient = cityClient;
    }*/
    private RMapReactive<String, City> cityMap;

    public CityService(RedissonReactiveClient client, CityClient cityClient) {
        //client.getMap("city", new TypedJsonJacksonCodec(String.class, City.class))은 Redis의 특정 맵을 가져오는 명령입니다.
        //TypedJsonJacksonCodec(String.class, City.class)는 Redis에 저장할 데이터를 (JSON 형식으로) 직렬화 및 역직렬화할 때 사용할 타입 정보를 제공합니다.
        //String.class: 맵의 키 타입.
        //City.class: 맵의 값 타입, 즉 도시 정보를 담고 있는 객체 타입.
        this.cityMap = client.getMapCache("city", new TypedJsonJacksonCodec(String.class, City.class));
        this.cityClient = cityClient;
    }

    public Mono<City> getCity(final String zipCode){
        return this.cityMap.get(zipCode)
                //.switchIfEmpty(this.cityClient.getCity(zipCode))
                //redis가 unavailable하면 cityClient.getCity(zipCode)를 호출
                .onErrorResume(ex -> this.cityClient.getCity(zipCode));
    }

    @Scheduled(fixedRate = 10_000)
    public void updateCity(){
        this.cityClient.getAll()
                .collectList()
                .map(list -> list.stream().collect(Collectors.toMap(City::getZip, Function.identity())))
                .flatMap(cityMap::putAll)
                .subscribe();
    }


/*    *//*
        get from cache
        if empty - get from db / source
            put it in cache
        return
     *//*
    public Mono<City> getCity(final String zipCode){
        return this.cityMap.get(zipCode)
                .switchIfEmpty(
                        //put: 이 메소드는 키-값 쌍을 Redis 맵에 저장하고, 이전에 같은 키로 저장되어 있던 값을 반환합니다.
                        // 만약 이전에 같은 키로 저장된 값이 없었다면 null을 반환합니다. 이 메소드는 이전 값이 필요한 경우에 사용됩니다.
                        //fastPut: 이 메소드는 키-값 쌍을 Redis 맵에 저장하고, 연산이 성공했는지 여부를 나타내는 boolean 값을 반환합니다.
                        // 이 메소드는 이전 값이 필요하지 않고, 단지 연산의 성공 여부만 확인하려는 경우에 사용됩니다.
                        //10초 동안 캐시에 저장
                        this.cityClient.getCity(zipCode)
                                .flatMap(c -> this.cityMap.fastPut(zipCode, c,
                                        10, TimeUnit.SECONDS).thenReturn(c))
                );
    }*/
}
