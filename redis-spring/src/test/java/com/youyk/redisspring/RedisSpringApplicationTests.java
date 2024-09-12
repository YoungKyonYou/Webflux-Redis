package com.youyk.redisspring;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest
class RedisSpringApplicationTests {
    @Autowired
    private ReactiveStringRedisTemplate template;
    @Autowired
    private RedissonReactiveClient client;

    @Test
    void contextLoads() {
        //ReactiveStringRedisTemplate는 Spring Data Redis에서 제공하는 클래스로, Redis와의 비동기(non-blocking) 통신을 가능하게 합니다.
        //이 코드는 Redis의 String 타입 데이터에 대한 연산을 수행할 수 있는 ReactiveValueOperations 인스턴스를 생성하는 것입니다.
        // 이 인스턴스를 통해 Redis에 저장된 String 타입의 데이터를 비동기적으로 읽고, 쓰고, 수정하고, 삭제하는 등의 연산을 수행할 수 있습니다.
        //여기서 첫 번째 String은 Redis에서 사용할 키의 타입을, 두 번째 String은 Redis에서 사용할 값의 타입을 나타냅니다.
        ReactiveValueOperations<String, String> valueOperations = this.template.opsForValue();

        long before = System.currentTimeMillis();

        Mono<Void> mono = Flux.range(1, 500_000)
                .flatMap(i -> valueOperations.increment("user:1:visit"))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();

        long after = System.currentTimeMillis();

        System.out.println((after - before) + " ms");
    }

    @RepeatedTest(3)
    void redissonTest() {
        RAtomicLongReactive atomicLong = this.client.getAtomicLong("user:2:visit");
        long before = System.currentTimeMillis();
        Mono<Void> mono = Flux.range(1, 500_000)
                .flatMap(i -> atomicLong.incrementAndGet()) // incr
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
        long after = System.currentTimeMillis();
        System.out.println((after - before) + " ms");
    }

}
