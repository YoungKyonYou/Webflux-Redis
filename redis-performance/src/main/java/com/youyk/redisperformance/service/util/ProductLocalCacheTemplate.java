package com.youyk.redisperformance.service.util;

import com.youyk.redisperformance.entity.Product;
import com.youyk.redisperformance.repository.ProductRepository;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;

import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product>{
    private final ProductRepository repository;
    private final RLocalCachedMap<Integer, Product> map;

    public ProductLocalCacheTemplate(ProductRepository repository, RedissonClient client) {
        this.repository = repository;
        //deprecated
/*        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);
        this.map = client.getLocalCachedMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class), mapOptions);*/
        this.map = client.getLocalCachedMap("product", LocalCachedMapOptions.defaults());

    }
    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        //Local Cache에서는 reactive 방식이 없음 따라서 Mono.justOrEmpty로 감싸줌
        return Mono.justOrEmpty(this.map.get(id));
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.repository.findById(id)
                .doOnNext(p -> product.setId(id))
                .flatMap(p -> this.repository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        // this.map.fastPutAsync는 비동기 방식
        // 따라서 Mono.create로 감싸줌
        /**
         * Mono.create는 Project Reactor에서 제공하는 API로, 비동기 작업을 수행하고
         * 그 결과를 Mono 스트림으로 반환하는 데 사용됩니다.
         * 이 메서드는 Consumer<MonoSink<T>>를 매개변수로 받습니다.
         * 여기서 MonoSink는 Mono 생성을 제어하는 인터페이스입니다.
         */
        return Mono.create(sink ->
                this.map.fastPutAsync(id, product)
                        //thenAccept 메서드는 CompletableFuture의 완료 시점에 실행할 작업을 지정합니다.
                        // 여기서는 sink.success(product)를 호출하여 Mono 스트림에 성공적으로 저장된 product를 전달합니다.
                        .thenAccept(b -> sink.success(product))
                        //exceptionally 메서드는 CompletableFuture에서 예외가 발생했을 때 실행할 작업을 지정합니다.
                        // 여기서는 sink.error(ex)를 호출하여 Mono 스트림에 예외를 전달합니다.
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        })
        );
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.repository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono.create(sink ->
                this.map.fastRemoveAsync(id)
                        .thenAccept(b -> sink.success())
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        })
        );
    }
}
