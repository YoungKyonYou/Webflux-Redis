package com.youyk.redisspring.fib.controller;

import com.youyk.redisspring.fib.service.FibService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("fib")
@RequiredArgsConstructor
public class FibController {
    private final FibService service;

    @GetMapping("{index}/{name}")
    public Mono<Integer> getFib(@PathVariable("index") int index, @PathVariable("name") String name){
        //fromSupplier : 값을 즉시 반환하는 동기적인 작업에 사용됩니다. 호출될 때 바로 값을 반환합니다.
        //계산 결과를 반환하거나 값을 가져오는 작업처럼, 결과값이 있는 경우에 적합합니다.
        return Mono.fromSupplier(() -> this.service.getFib(index, name));
    }

    @GetMapping("{index}/clear")
    public Mono<Void> clearCache(@PathVariable("index") int index){
        //fromRunnable : Runnable을 인자로 받으며, 이 Runnable은 실행될 때 단순히 작업을 수행하지만, 값을 반환하지 않습니다.
        //캐시를 지우는 작업이나 로그를 남기는 작업처럼, 수행만 하면 되고 결과가 필요 없는 경우에 적합합니다.
        return Mono.fromRunnable(() -> this.service.clearCache(index));
    }
}
