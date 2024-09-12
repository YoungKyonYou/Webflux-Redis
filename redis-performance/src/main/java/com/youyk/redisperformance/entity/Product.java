package com.youyk.redisperformance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Table //이걸 안 붙여주면 에러남
//Parameter 0 of constructor in com.youyk.redisperformance.service.ProductServiceV1 required a bean of type 'com.youyk.redisperformance.repository.ProductRepository' that could not be found.
//위와 같은 에러 발생
//우리는 gradle에 보면 하나는 spring data redis가 있고 하나는 mysql를 쓰고 있다. 즉 우리는 두개의 R2DBC가 있는 것이다.
//그래서 두개가 ProductRepository를 찾으려고 하는데 둘 중 어떤 것을 사용해야 하는지 모르기 때문에 에러가 발생하는 것이다.
//그래서 @Table을 붙여주어 이것은 R2DBC를 위한 것이지 redis를 위한 것이 아니라고 명시해줘야 하는 것이다.
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private Integer id;
    private String description;
    private double price;
}
