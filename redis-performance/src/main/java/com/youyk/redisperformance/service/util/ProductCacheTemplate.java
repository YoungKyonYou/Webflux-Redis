package com.youyk.redisperformance.service.util;

import com.youyk.redisperformance.entity.Product;
import com.youyk.redisperformance.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductCacheTemplate extends CacheTemplate<Integer, Product>{
    private final ProductRepository repository;
    private final RMapReactive<Integer, Product> map;

    public ProductCacheTemplate(ProductRepository repository, RedissonReactiveClient client) {
        this.repository = repository;
        this.map = client.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return this.map.get(id);
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.repository.findById(id)
                .doOnNext(p -> product.setId(id))
                .flatMap(p -> this.repository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return this.map.fastPut(id, product).thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.repository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return this.map.fastRemove(id).then();
    }
}
