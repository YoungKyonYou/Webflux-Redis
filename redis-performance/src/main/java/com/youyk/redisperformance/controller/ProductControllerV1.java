package com.youyk.redisperformance.controller;

import com.youyk.redisperformance.entity.Product;
import com.youyk.redisperformance.service.ProductServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("product/v1")
public class ProductControllerV1 {
    private final ProductServiceV1 service;

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable("id") int id){
        return this.service.getProduct(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable("id") int id, @RequestBody Mono<Product> productMono){
        return this.service.updateProduct(id, productMono);
    }
}
