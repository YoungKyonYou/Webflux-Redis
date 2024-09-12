package com.youyk.redisperformance.service;

import com.youyk.redisperformance.entity.Product;
import com.youyk.redisperformance.service.util.CacheTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceV3 {
    @Qualifier("productLocalCacheTemplate")
    private final CacheTemplate<Integer, Product> cacheTemplate;

    //GET
    public Mono<Product> getProduct(int id){
        return this.cacheTemplate.get(id);
    }

    //PUT
    public Mono<Product> updateProduct(int id, Mono<Product> productMono){
        return productMono
                .flatMap(p -> this.cacheTemplate.update(id, p));
    }

    //DELETE
    public Mono<Void> deleteProduct(int id){
        return this.cacheTemplate.delete(id);
    }

}

