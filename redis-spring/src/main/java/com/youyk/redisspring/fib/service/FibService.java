package com.youyk.redisspring.fib.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FibService {
    /**
     *  @Cacheable 어노테이션이 붙은 메소드는 그 메소드의 파라미터 값에 따른 결과를 캐시에 저장합니다.
     *  즉, 메소드의 파라미터가 캐시의 키가 되고, 메소드의 반환 값이 캐시의 값이 됩니다.
     *  예를 들어, getFib(int index) 메소드에 @Cacheable("math:fib") 어노테이션이 붙어 있다면,
     *  getFib(5)를 호출하면 그 결과 값이 "math:fib" 캐시에 키 '5'로 저장됩니다
     *
     *  key = "#index"를 사용하는 이유는 보통은 메서드 파라미터로 키값을 구성하는데 여기서는 name를 제외한 index만을 key로 사용하라고 지정하는 것
     */
    //have a strategy for cache eviction
    @Cacheable(value ="math:fib", key = "#index")
    public int getFib(int index, String name){
        System.out.println("calculating fib for "+index + ", name : "+ name);
        return this.fib(index);
    }

    //이 메서드가 호출되면 math:fib 으로 가서 index에 해당하는 캐시를 삭제한다.
    // PUT / POST / PATCH / DELETE 등의 요청이 들어왔을 때 해당 캐시를 삭제하는 용도로 사용
    @CacheEvict(value = "math:fib",  key = "#index")
    public void clearCache(int index){
        System.out.println("clearing hash key");
    }


    //매 5초마다 math:fib 캐시에 있는 모든 캐시를 삭제한다.
    @Scheduled(fixedRate = 10_000)
    @CacheEvict(value = "math:fib", allEntries = true)
    public void clearCache(){
        System.out.println("clearing all fib keys");
    }

    // intentional 2^n
    private int fib(int index){
        if(index < 2){
            return index;
        }
        return fib(index - 1) + fib(index - 2);
    }
}
