
package com.petclinic.bffapigateway.domainclientlayer;

import com.petclinic.bffapigateway.dtos.Inventory.InventoryResponseDTO;
import com.petclinic.bffapigateway.dtos.Inventory.InventoryRequestDTO;
import com.petclinic.bffapigateway.dtos.Inventory.ProductRequestDTO;
import com.petclinic.bffapigateway.dtos.Inventory.ProductResponseDTO;
import com.petclinic.bffapigateway.exceptions.InvalidInputsInventoryException;
import com.petclinic.bffapigateway.exceptions.InventoryNotFoundException;
import com.petclinic.bffapigateway.exceptions.ProductListNotFoundException;
import com.petclinic.bffapigateway.utils.Rethrower;
import org.springframework.beans.factory.annotation.Autowired;
import com.petclinic.bffapigateway.dtos.Inventory.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.nio.channels.FileChannel;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;


@Component
public class InventoryServiceClient {
    private final WebClient webClient;
    private String inventoryServiceUrl;
    @Autowired
    private Rethrower rethrower;

    public InventoryServiceClient(
            @Value("${app.inventory-service.host}") String inventoryServiceHost,
            @Value("${app.inventory-service.port}") String inventoryServicePort
    ) {

        inventoryServiceUrl = "http://" + inventoryServiceHost + ":" + inventoryServicePort + "/inventory";
        this.webClient = WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .build();
    }


    public Mono<InventoryResponseDTO> getInventoryById(final String inventoryId) {
        return webClient.get()
                .uri(inventoryServiceUrl + "/{inventoryId}", inventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new InventoryNotFoundException(ex.get("message").toString(), NOT_FOUND)))
                .bodyToMono(InventoryResponseDTO.class);
    }


    public Mono<ProductResponseDTO> getProductByProductIdInInventory(final String inventoryId, final String productId) {
        return webClient.get()
                .uri(inventoryServiceUrl + "/{inventoryId}/products/{productId}", inventoryId, productId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new ProductListNotFoundException(ex.get("message").toString(), NOT_FOUND)))
                .bodyToMono(ProductResponseDTO.class);
    }

    public Mono<ProductResponseDTO> addProductToInventory(final ProductRequestDTO model, final String inventoryId){

        return webClient.post()
                .uri(inventoryServiceUrl + "/{inventoryId}/products", inventoryId)
                .body(Mono.just(model),ProductRequestDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new InvalidInputsInventoryException(ex.get("message").toString(), BAD_REQUEST)))
                .bodyToMono(ProductResponseDTO.class);
    }




    public Mono<InventoryResponseDTO> addInventory(final InventoryRequestDTO model){
        return webClient.post()
                .uri(inventoryServiceUrl)
                .body(Mono.just(model),InventoryRequestDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new InvalidInputsInventoryException(ex.get("message").toString(), BAD_REQUEST)))
                .bodyToMono(InventoryResponseDTO.class);
    }



    public Mono<InventoryResponseDTO> updateInventory(InventoryRequestDTO model, String inventoryId){
        return webClient.put()
                .uri(inventoryServiceUrl + "/{inventoryId}" , inventoryId)
                .body(Mono.just(model),InventoryRequestDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new InvalidInputsInventoryException(ex.get("message").toString(), BAD_REQUEST)))
                .bodyToMono(InventoryResponseDTO.class);
    }





    public Mono<ProductResponseDTO> updateProductInInventory(ProductRequestDTO model, String inventoryId, String productId){


        return webClient
                .put()
                .uri(inventoryServiceUrl + "/{inventoryId}/products/{productId}", inventoryId, productId)
                .body(Mono.just(model),ProductRequestDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new InvalidInputsInventoryException(ex.get("message").toString(), BAD_REQUEST)))
                .bodyToMono(ProductResponseDTO.class);
    }

    public Mono<Void> deleteProductInInventory(final String inventoryId, final String productId){
        return webClient.delete()
                .uri(inventoryServiceUrl + "/{inventoryId}/products/{productId}", inventoryId, productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new ProductListNotFoundException(ex.get("message").toString(), NOT_FOUND)))
                .bodyToMono(Void.class);
    }


    public Flux<ProductResponseDTO> getProductsInInventoryByInventoryIdAndProductsField(final String inventoryId, final String productName, final Double productPrice, final Integer productQuantity, final Double productSalePrice){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(inventoryServiceUrl + "/{inventoryType}/products")
                .queryParamIfPresent("productName", Optional.ofNullable(productName))
                .queryParamIfPresent("productPrice", Optional.ofNullable(productPrice))
                .queryParamIfPresent("productQuantity", Optional.ofNullable(productQuantity))
                .queryParamIfPresent("productSalePrice", Optional.ofNullable(productSalePrice));

        return webClient.get()
                .uri(uriBuilder.buildAndExpand(inventoryId).toUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new ProductListNotFoundException(ex.get("message").toString(), NOT_FOUND)))
                .bodyToFlux(ProductResponseDTO.class);
    }

    public Flux<ProductResponseDTO> getProductsInInventoryByInventoryIdAndProductFieldPagination(final String inventoryId,
                                                                                                 final String productName,
                                                                                                 final Double productPrice,
                                                                                                 final Integer productQuantity,
                                                                                                 final Optional<Integer> page,
                                                                                                 final Optional<Integer> size){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(inventoryServiceUrl + "/" + inventoryId + "/products-pagination");
        if (page.isPresent() && size.isPresent()) {
            uriBuilder.queryParam("page", page.get());
            uriBuilder.queryParam("size", size.get());
        }
        if (productName != null) {
            uriBuilder.queryParam("productName", productName);
        }
        if (productPrice != null) {
            uriBuilder.queryParam("productPrice", productPrice);
        }
        if (productQuantity != null) {
            uriBuilder.queryParam("productQuantity", productQuantity);
        }

        return webClient.get()
                .uri(uriBuilder.buildAndExpand(inventoryId).toUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new ProductListNotFoundException(ex.get("message").toString(), NOT_FOUND)))
                .bodyToFlux(ProductResponseDTO.class);
    }

    public Mono<Long> getTotalNumberOfProductsWithRequestParams(final String inventoryId,
                                                                final String productName,
                                                                final Double productPrice,
                                                                final Integer productQuantity){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(inventoryServiceUrl + "/" + inventoryId + "/products-count")
                .queryParamIfPresent("productName", Optional.ofNullable(productName))
                .queryParamIfPresent("productPrice", Optional.ofNullable(productPrice))
                .queryParamIfPresent("productQuantity", Optional.ofNullable(productQuantity));

        return webClient.get()
                .uri(uriBuilder.buildAndExpand(inventoryId).toUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new ProductListNotFoundException(ex.get("message").toString(), NOT_FOUND)))
                .bodyToMono(Long.class);
    }
    public Flux<InventoryResponseDTO> searchInventory(
            final Optional<Integer> page,
            final Optional<Integer> size,
            final String inventoryName,
            final String inventoryType,
            final String inventoryDescription
    ) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(inventoryServiceUrl)
                .queryParamIfPresent("page", page)
                .queryParamIfPresent("size", size)
                .queryParamIfPresent("inventoryName", Optional.ofNullable(inventoryName))
                .queryParamIfPresent("inventoryType", Optional.ofNullable(inventoryType))
                .queryParamIfPresent("inventoryDescription", Optional.ofNullable(inventoryDescription));


        return webClient.get()
                .uri(uriBuilder.buildAndExpand().toUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                // Consider adding error-handling logic here if needed.
                .bodyToFlux(InventoryResponseDTO.class);
    }


    //delete all

    public Mono<Void> deleteAllProductForInventory(final String inventoryId) {
        return webClient.delete()
                .uri(inventoryServiceUrl + "/{inventoryId}/products", inventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> rethrower.rethrow(resp, ex -> new ProductListNotFoundException(ex.get("message").toString(), NOT_FOUND)))
                .bodyToMono(Void.class);
    }
    public Mono<Void> deleteAllInventories() {
        return webClient.delete()
                .uri(inventoryServiceUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class);
    }
    public Mono<InventoryTypeResponseDTO> addInventoryType(InventoryTypeRequestDTO inventoryTypeRequestDTO){
        return webClient.post()
                .uri(inventoryServiceUrl + "/type")
                .body(Mono.just(inventoryTypeRequestDTO),InventoryTypeRequestDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(InventoryTypeResponseDTO.class);
    }

    public Flux<InventoryTypeResponseDTO> getAllInventoryTypes(){
        return webClient.get()
                .uri(inventoryServiceUrl + "/type")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(InventoryTypeResponseDTO.class);
    }

    public Flux<InventoryNameResponseDTO> getAllInventoryNames(){
        return webClient.get()
                .uri(inventoryServiceUrl + "/name")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(InventoryNameResponseDTO.class);
    }



    public Mono<Void> deleteInventoryByInventoryId(String inventoryId){
        return webClient.delete()
                .uri(inventoryServiceUrl + "/{inventoryId}", inventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> rethrower.rethrow(resp, ex -> new NotFoundException(ex.get("message").toString())))
                .bodyToMono(Void.class);
    }


    public Mono<InventoryResponseDTO> addSupplyToInventoryByName(String inventoryName, SupplyRequestDTO supplyRequestDTO) {
        return webClient.post()
                .uri(inventoryServiceUrl + "/{inventoryName}/supplies", inventoryName)
                .body(Mono.just(supplyRequestDTO), SupplyRequestDTO.class)
                .retrieve()
                .bodyToMono(InventoryResponseDTO.class);
    }


    public Flux<SupplyResponseDTO> getSuppliesByInventoryName(String inventoryName) {
        return webClient.get()
                .uri("/{inventoryName}/supplies", inventoryName)
                .retrieve()
                .bodyToFlux(SupplyResponseDTO.class);
    }
  
  public Flux<ProductResponseDTO> getLowStockProducts(String inventoryId, int stockThreshold) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(inventoryServiceUrl + "/{inventoryId}/products/lowstock")
                .queryParam("threshold", stockThreshold);

        return webClient.get()
                .uri(uriBuilder.buildAndExpand(inventoryId).toUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new NotFoundException("No products below threshold in inventory: " + inventoryId)))
                .bodyToFlux(ProductResponseDTO.class);
    }

}
