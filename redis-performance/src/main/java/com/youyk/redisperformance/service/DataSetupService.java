package com.youyk.redisperformance.service;

import com.youyk.redisperformance.entity.Product;
import com.youyk.redisperformance.repository.ProductRepository;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class DataSetupService implements CommandLineRunner {
    private final ProductRepository repository;
    private final R2dbcEntityTemplate template;

    @Value("classpath:sql/schema.sql")
    private Resource resource;


    @Override
    public void run(String... args) throws Exception {
        String query = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(query);

        Mono<Void> insert = Flux.range(1, 1000)
                .map(i -> new Product(null, "product" + i, ThreadLocalRandom.current().nextInt(1, 100)))
                .collectList()
                //flatMapMany를 사용하는 이유는 collectList() 에서 Mono<List<Product>>를 반환하고 saveAll에서 Flux를 반환하기 때문입니다.
                // 즉  Mono<List<Product>>를 Flux<Product>로 변환합니다.
                .flatMapMany(this.repository::saveAll)
                .then();

        this.template.getDatabaseClient()
                .sql(query)
                //이 then()은 SQL 쿼리 실행이 완료된 후 그 결과를 무시하고, 다음 Mono나 Flux를 연결하기 위해 사용됩니다.
                .then()
                //SQL 쿼리 실행이 완료된 후, insert 작업(즉, 1000개의 Product를 데이터베이스에 저장)을 연결합니다.
                // 다시 말해, SQL 쿼리가 성공적으로 완료된 후에만 insert 작업이 시작되도록 합니다.
                .then(insert)
                .doFinally(s -> System.out.println("Data Setup Done "+ s))
                .subscribe();
    }
}
