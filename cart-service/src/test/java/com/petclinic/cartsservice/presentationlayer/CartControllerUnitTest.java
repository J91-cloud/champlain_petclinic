package com.petclinic.cartsservice.presentationlayer;

import com.petclinic.cartsservice.businesslayer.CartService;
import com.petclinic.cartsservice.dataaccesslayer.Cart;
import com.petclinic.cartsservice.domainclientlayer.ProductResponseModel;
import com.petclinic.cartsservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = CartController.class)
class CartControllerUnitTest {


    @MockBean
    private CartService cartService;

    @Autowired
    private WebTestClient webTestClient;


    private final String NOT_FOUND_CART_ID = "98f7b33a-d62a-420a-a84a-05a27c85fc92";

    @Test
    public void whenGetCartByCartId_thenReturnCartResponseModel() {
        // arrange
        List<String> productIds = List.of("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223");

          Cart cart = Cart.builder()
                .cartId("98f7b33a-d62a-420a-a84a-05a27c85fc91")
                .productIds(productIds)
                .customerId("1")
                .build();

        ProductResponseModel product1 = ProductResponseModel.builder()
                .productId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .productName("Web Services")
                .productDescription("Learn how to create web services")
                .productSalePrice(100.00)
                .build();

        List<ProductResponseModel> products = List.of(product1);

        CartResponseModel cartResponseModel = CartResponseModel.builder()
                .cartId(cart.getCartId())
                .customerId("1")
                .products(products)
                .build();

        when(cartService.getCartByCartId(cart.getCartId())).thenReturn(Mono.just(cartResponseModel));

        // act & assert
        webTestClient
                .get()
                .uri("/api/v1/carts/" + cart.getCartId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CartResponseModel.class)
                .value(result -> {
                    assertEquals(cart.getCartId(), result.getCartId());
                    assertEquals(cart.getCustomerId(), result.getCustomerId());
                    assertEquals(cart.getProductIds(), result.getProducts().stream().map(ProductResponseModel::getProductId).toList());
                });
    }

    @Test
    void whenGetCartByCartId_withNonExistingCartId_thenReturnNotFound() {
        // Arrange
        when(cartService.getCartByCartId(NOT_FOUND_CART_ID))
                .thenReturn(Mono.error(new NotFoundException("Enrollment id not found: " + NOT_FOUND_CART_ID)));

        // Act & Assert
        webTestClient
                .get()
                .uri("/api/v1/carts/" + NOT_FOUND_CART_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetCartByCartId_withIncorrectCartId_thenThrowIllegalArgumentException() {
        when(cartService.getCartByCartId("incorrect-cart-id"))
                .thenReturn(Mono.error(new IllegalArgumentException("Provided cart id is invalid: " + "incorrect-cart-id")));

        // Act & Assert
        webTestClient
                .get()
                .uri("/api/v1/carts/" + "incorrect-cart-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422);    }

    @Test
    void whenCreateNewCart_withValidCustomerId_thenReturnCartResponse() {
        CartResponseModel expectedCartResponseModel = new CartResponseModel();
        expectedCartResponseModel.setCartId("12345");
        expectedCartResponseModel.setCustomerId("123");

        when(cartService.createNewCart(any(CartRequestModel.class)))
                .thenReturn(Mono.just(expectedCartResponseModel));

        String json = """
                  {
                    "customerId":"123"
                  }
                """;

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/v1/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(201);
    }

    @Test
    public void getCartItemCount_Success() {
        when(cartService.getCartItemCount("cart1")).thenReturn(Mono.just(3));

        webTestClient.get().uri("/api/v1/carts/cart1/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.itemCount").isEqualTo(3);
    }

    @Test
    public void getCartItemCount_CartNotFound() {
        when(cartService.getCartItemCount("cart1")).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/v1/carts/cart1/count")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void whenDeleteCartByIdWithExistingId_thenReturnCartResponseModel(){
        List<String> productIds = List.of("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223");

        Cart cart = Cart.builder()
                .cartId("98f7b33a-d62a-420a-a84a-05a27c85fc91")
                .productIds(productIds)
                .customerId("1")
                .build();

        ProductResponseModel product1 = ProductResponseModel.builder()
                .productId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .productName("Web Services")
                .productDescription("Learn how to create web services")
                .productSalePrice(100.00)
                .build();

        List<ProductResponseModel> products = List.of(product1);

        CartResponseModel cartResponseModel = CartResponseModel.builder()
                .cartId(cart.getCartId())
                .customerId("1")
                .products(products)
                .build();
        when(cartService.deleteCartByCartId(cart.getCartId()))
                .thenReturn(Mono.just(cartResponseModel));

        webTestClient
                .delete()
                .uri("/api/v1/carts/" + cart.getCartId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CartResponseModel.class)
                .isEqualTo(cartResponseModel);

        verify(cartService, times(1)).deleteCartByCartId(cartResponseModel.getCartId());
    }

    @Test
    public void whenDeleteCartByIdWithInvalidId_ThenReturnEmptyMono(){
        String cartId = "98f7b33a-d62a-420a-a84a-05a27c85fc";

        when(cartService.deleteCartByCartId(cartId))
                .thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/api/v1/carts/" + cartId)
                .accept()
                .exchange()
                .expectStatus().isEqualTo(422);

        verify(cartService, times(0)).deleteCartByCartId(cartId);
    }


}