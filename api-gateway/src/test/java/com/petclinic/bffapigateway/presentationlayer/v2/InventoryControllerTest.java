package com.petclinic.bffapigateway.presentationlayer.v2;

import com.petclinic.bffapigateway.domainclientlayer.InventoryServiceClient;
import com.petclinic.bffapigateway.dtos.Inventory.InventoryRequestDTO;
import com.petclinic.bffapigateway.dtos.Inventory.InventoryResponseDTO;
import com.petclinic.bffapigateway.dtos.Inventory.InventoryTypeResponseDTO;
import com.petclinic.bffapigateway.dtos.Inventory.ProductResponseDTO;
import com.petclinic.bffapigateway.exceptions.InventoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        InventoryController.class,
        InventoryServiceClient.class
})
@WebFluxTest(controllers = InventoryController.class)
@AutoConfigureWebTestClient
public class InventoryControllerTest {
    @Autowired
    private WebTestClient client;
    @MockBean
    private InventoryServiceClient inventoryServiceClient;
    private final String baseInventoryURL = "/api/v2/gateway/inventories";

    private InventoryResponseDTO buildInventoryDTO(){
        return InventoryResponseDTO.builder()
                .inventoryId("1")
                .inventoryName("invt1")
                .inventoryType("Internal")
                .inventoryDescription("invtone")
                .build();
    }
    private List<InventoryTypeResponseDTO> buildInventoryTypeResponseDTOList(){
        return List.of(InventoryTypeResponseDTO.builder()
                .type("Internal")
                .typeId("1")
                .build(), InventoryTypeResponseDTO.builder().typeId("2").type("External").build());
    }

    @Test
    void deleteAllInventories_shouldSucceed() {
        // Arrange
        when(inventoryServiceClient.deleteAllInventories())
                .thenReturn(Mono.empty());  // Using Mono.empty() to simulate a void return (successful deletion without a return value).

        // Act
        client.delete()
                .uri("/api/v2/gateway/inventories")  // Assuming the endpoint for deleting all inventories is the same without an ID.
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        // Assert
        verify(inventoryServiceClient, times(1))
                .deleteAllInventories();
    }


    @Test
    void getAllInventories_withValidPageSize_and_PageNumber_shouldReturnInventories() {
        //Arrange
        Optional<Integer> page = Optional.of(0);
        Optional<Integer> size = Optional.of(2);
        when(inventoryServiceClient.searchInventory(page, size, null, null, null))
                .thenReturn(Flux.just(buildInventoryDTO()));

        // Act
        client.get()
                .uri(baseInventoryURL + "?page=0&size=2")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .acceptCharset(StandardCharsets.UTF_8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryResponseDTO.class)
                .hasSize(1)
                .contains(buildInventoryDTO());

        // Assert
        verify(inventoryServiceClient, times(1))
                .searchInventory(eq(page), eq(size), eq(null), eq(null), eq(null));
    }

    @Test
    void getAllInventories_with_ValidQueryParams_InventoryName_InventoryType_InventoryDescription_shouldReturnOneInventory() {
        // Arrange
        Optional<Integer> page = Optional.of(0);
        Optional<Integer> size = Optional.of(2);
        when(inventoryServiceClient.searchInventory(page, size, "invt1", "Internal", "invtone"))
                .thenReturn(Flux.just(buildInventoryDTO()));

        // Act
        client.get()
                .uri(baseInventoryURL + "?page=0&size=2&inventoryName=invt1&inventoryType=Internal&inventoryDescription=invtone")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .acceptCharset(StandardCharsets.UTF_8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryResponseDTO.class)
                .hasSize(1)
                .contains(buildInventoryDTO());

        // Assert
        verify(inventoryServiceClient, times(1))
                .searchInventory(eq(page), eq(size), eq("invt1"), eq("Internal"), eq("invtone"));
    }

    @Test
    void getAllInventories_with_Invalid_QueryParams_shouldReturnEmptyList() {
        // Arrange
        Optional<Integer> page = Optional.of(0);
        Optional<Integer> size = Optional.of(2);
        when(inventoryServiceClient.searchInventory(page, size, "invalid", "invalid", "invalid"))
                .thenReturn(Flux.empty());

        // Act
        client.get()
                .uri(baseInventoryURL + "?page=0&size=2&inventoryName=invalid&inventoryType=invalid&inventoryDescription=invalid")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .acceptCharset(StandardCharsets.UTF_8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryResponseDTO.class)
                .hasSize(0);

        // Assert
        verify(inventoryServiceClient, times(1))
                .searchInventory(eq(page), eq(size), eq("invalid"), eq("invalid"), eq("invalid"));
    }

    @Test
    void getAllInventoryTypes_shouldReturnInventoryTypes() {
        // Arrange
        when(inventoryServiceClient.getAllInventoryTypes())
                .thenReturn(Flux.fromIterable(buildInventoryTypeResponseDTOList()));

        // Act
        client.get()
                .uri(baseInventoryURL + "/types")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .acceptCharset(StandardCharsets.UTF_8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryTypeResponseDTO.class)
                .hasSize(2);

        // Assert
        verify(inventoryServiceClient, times(1))
                .getAllInventoryTypes();
    }

    @Test
    void deleteInventoryById_WithValidInventoryId_ShouldSucceed() {
        // Arrange
        String inventoryId = "1";
        when(inventoryServiceClient.deleteInventoryByInventoryId(inventoryId))
                .thenReturn(Mono.empty());

        // Act
        client.delete()
                .uri(baseInventoryURL + "/" + inventoryId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        // Assert
        verify(inventoryServiceClient, times(1))
                .deleteInventoryByInventoryId(eq(inventoryId));
    }

    @Test
    void deleteInventoryById_WithInvalid_ShouldReturnNotFound() {
        // Arrange
        String inventoryId = "invalid";
        when(inventoryServiceClient.deleteInventoryByInventoryId(inventoryId))
                .thenThrow(new NotFoundException("Inventory not found"));

        // Act
        client.delete()
                .uri(baseInventoryURL + "/" + inventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        // Assert
        verify(inventoryServiceClient, times(1))
                .deleteInventoryByInventoryId(inventoryId);
    }

    @Test
    void getInventoryById_withValidId_shouldReturnInventory() {
        // Arrange
        String inventoryId = "1";
        InventoryResponseDTO inventory = buildInventoryDTO();
        when(inventoryServiceClient.getInventoryById(inventoryId))
                .thenReturn(Mono.just(inventory));

        // Act
        client.get()
                .uri(baseInventoryURL + "/" + inventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InventoryResponseDTO.class)
                .isEqualTo(inventory);

        // Assert
        verify(inventoryServiceClient, times(1))
                .getInventoryById(eq(inventoryId));
    }

    @Test
    void getInventoryById_withInvalidIdFormat_shouldReturnBadRequest() {
        // Arrange
        String invalidInventoryId = "invalid-id-format";
        when(inventoryServiceClient.getInventoryById(invalidInventoryId))
                .thenReturn(Mono.empty());

        // Act
        client.get()
                .uri(baseInventoryURL + "/" + invalidInventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        // Assert
        verify(inventoryServiceClient, times(1))
                .getInventoryById(eq(invalidInventoryId));
    }

    @Test
    void getInventoryById_withNonExistentId_shouldReturnNotFound() {
        // Arrange
        String nonExistentInventoryId = "non-existent-id";
        when(inventoryServiceClient.getInventoryById(nonExistentInventoryId))
                .thenReturn(Mono.empty());

        // Act
        client.get()
                .uri(baseInventoryURL + "/" + nonExistentInventoryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        // Assert
        verify(inventoryServiceClient, times(1))
                .getInventoryById(eq(nonExistentInventoryId));
    }

    @Test
    void updateInventoryById_withValidId_shouldReturnUpdatedInventory() {
        // Arrange
        String inventoryId = "dfa0a7e3-5a40-4b86-881e-9549ecda5e4b";
        InventoryRequestDTO updateRequest = InventoryRequestDTO.builder()
                .inventoryName("updatedName")
                .inventoryType("updatedType")
                .inventoryDescription("updatedDescription")
                .build();
        InventoryResponseDTO updatedInventory = InventoryResponseDTO.builder()
                .inventoryId(inventoryId)
                .inventoryName("updatedName")
                .inventoryType("updatedType")
                .inventoryDescription("updatedDescription")
                .build();

        when(inventoryServiceClient.updateInventory(eq(updateRequest), eq(inventoryId)))
                .thenReturn(Mono.just(updatedInventory));

        // Act
        client.put()
                .uri(baseInventoryURL + "/" + inventoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InventoryResponseDTO.class)
                .isEqualTo(updatedInventory);

        // Assert
        verify(inventoryServiceClient, times(1))
                .updateInventory(eq(updateRequest), eq(inventoryId));
    }

    @Test
    void updateInventoryById_withServiceLayerFailure_shouldReturnInternalServerError() {
        // Arrange
        String validInventoryId = "dfa0a7e3-5a40-4b86-881e-9549ecda5e4b";
        InventoryRequestDTO updateRequest = InventoryRequestDTO.builder()
                .inventoryName("updatedName")
                .inventoryType("updatedType")
                .inventoryDescription("updatedDescription")
                .build();

        when(inventoryServiceClient.updateInventory(eq(updateRequest), eq(validInventoryId)))
                .thenReturn(Mono.error(new RuntimeException("Service failure")));  // Simulating service failure.

        // Act
        client.put()
                .uri(baseInventoryURL + "/" + validInventoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError();  // Expecting 500 Internal Server Error.

        // Assert
        verify(inventoryServiceClient, times(1))
                .updateInventory(eq(updateRequest), eq(validInventoryId));
    }

}