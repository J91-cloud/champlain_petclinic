package com.petclinic.bffapigateway.presentationlayer.v2;


import com.petclinic.bffapigateway.domainclientlayer.CustomersServiceClient;
import com.petclinic.bffapigateway.domainclientlayer.VetsServiceClient;
import com.petclinic.bffapigateway.dtos.CustomerDTOs.OwnerRequestDTO;
import com.petclinic.bffapigateway.dtos.CustomerDTOs.OwnerResponseDTO;
import com.petclinic.bffapigateway.dtos.Vets.VetRequestDTO;
import com.petclinic.bffapigateway.dtos.Vets.VetResponseDTO;
import com.petclinic.bffapigateway.exceptions.InvalidInputException;
import com.petclinic.bffapigateway.utils.Security.Annotations.IsUserSpecific;
import com.petclinic.bffapigateway.utils.Security.Annotations.SecuredEndpoint;
import com.petclinic.bffapigateway.utils.Security.Variables.Roles;
import com.petclinic.bffapigateway.utils.VetsEntityDtoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2/gateway/vets")
@Validated
@CrossOrigin(origins = "http://localhost:3000, http://localhost:80")
public class VetController {


    private final VetsServiceClient vetsServiceClient;


    @SecuredEndpoint(allowedRoles = {Roles.ANONYMOUS})
    @GetMapping()
    public Flux<VetResponseDTO> getVets(){
        return vetsServiceClient.getVets();
    }


    @SecuredEndpoint(allowedRoles = {Roles.ADMIN})
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<VetResponseDTO>> addVet(@RequestBody Mono<VetRequestDTO> vetRequestDTO){
        return vetsServiceClient.addVet(vetRequestDTO)
                .map(v -> ResponseEntity.status(HttpStatus.CREATED).body(v))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }


    @SecuredEndpoint(allowedRoles = {Roles.ADMIN, Roles.VET})
    //@IsUserSpecific(idToMatch = {"vetId"}, bypassRoles = {Roles.ADMIN})
    @PutMapping(value = "/{vetId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<VetResponseDTO>> updateVet(
            @RequestBody Mono<VetRequestDTO> vetRequestDTOMono,
            @PathVariable String vetId){

        return Mono.just(vetId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided vet Id is invalid " + vetId)))
                .flatMap(id -> vetsServiceClient.updateVet(id, vetRequestDTOMono))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }


    @SecuredEndpoint(allowedRoles = {Roles.ADMIN})
    @DeleteMapping(value = "{vetId}")
    public Mono<ResponseEntity<Void>> deleteVet(@PathVariable String vetId) {
        return vetsServiceClient.deleteVet(VetsEntityDtoUtil.verifyId(vetId))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @SecuredEndpoint(allowedRoles = {Roles.ADMIN})
    @PostMapping(value = "{vetId}/photos/{photoName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> addPhoto(
            @PathVariable String vetId,
            @PathVariable String photoName,
            @RequestParam("image") MultipartFile image) throws IOException {


        // Convert MultipartFile to Resource
        Mono<Resource> resourceMono = Mono.just(new ByteArrayResource(image.getBytes()));


        return vetsServiceClient.addPhotoToVet(vetId, photoName, resourceMono)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @SecuredEndpoint(allowedRoles = {Roles.ANONYMOUS})
    @GetMapping(value = "{vetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<VetResponseDTO>> getVetByVetId(@PathVariable String vetId) {
        return vetsServiceClient.getVetByVetId(vetId)
                .map(vet -> ResponseEntity.status(HttpStatus.OK).body(vet))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

}
