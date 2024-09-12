package com.youyk.redisperformance.service;

import com.youyk.redisperformance.entity.Product;
import com.youyk.redisperformance.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceV1 {
    private final ProductRepository repository;

    public Mono<Product> getProduct(int id){
        return this.repository.findById(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono){
        return this.repository.findById(id)
                .flatMap(p -> productMono.doOnNext(pr -> pr.setId(id)))
                .flatMap(this.repository::save);
    }
}
