package com.petclinic.cartsservice.dataaccesslayer;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import reactor.core.publisher.Flux;

import java.util.List;


@Document(collection = "cart")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    private String id;

    private String cartId;
    private List<String> productIds;
    private String customerId;
}
