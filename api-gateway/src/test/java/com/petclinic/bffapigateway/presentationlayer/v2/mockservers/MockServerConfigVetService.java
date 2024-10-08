package com.petclinic.bffapigateway.presentationlayer.v2.mockservers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.bffapigateway.dtos.Vets.SpecialtyDTO;
import com.petclinic.bffapigateway.dtos.Vets.VetResponseDTO;
import com.petclinic.bffapigateway.dtos.Vets.Workday;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import java.util.Set;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class MockServerConfigVetService {

    private static final Integer VET_SERVICE_SERVER_PORT = 7002;

    private final ClientAndServer clientAndServer;

    private final MockServerClient mockServerClient_VetService = new MockServerClient("localhost", VET_SERVICE_SERVER_PORT);

    public MockServerConfigVetService() {
        this.clientAndServer = ClientAndServer.startClientAndServer(VET_SERVICE_SERVER_PORT);

    }


    public void registerGetVetsEndpoint() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/vets")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json("["
                                        + "{"
                                        + "\"vetId\":\"2e26e7a2-8c6e-4e2d-8d60-ad0882e295eb\","
                                        + "\"vetBillId\":\"bill001\","
                                        + "\"firstName\":\"John\","
                                        + "\"lastName\":\"Doe\","
                                        + "\"email\":\"john.doe@example.com\","
                                        + "\"phoneNumber\":\"1234567890\","
                                        + "\"resume\":\"Specialist in dermatology\","
                                        + "\"workday\":["
                                        + "\"Monday\", \"Wednesday\""
                                        + "],"
                                        + "\"workHoursJson\":\"08:00-16:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"dermatology\","
                                        + "\"name\":\"Dermatology\""
                                        + "}"
                                        + "],"
                                        + "\"photoDefault\":false"
                                        + "},"
                                        + "{"
                                        + "\"vetId\":\"3f87b21b-8f44-4bde-b2a6-abc123ef5678\","
                                        + "\"vetBillId\":\"bill002\","
                                        + "\"firstName\":\"Jane\","
                                        + "\"lastName\":\"Smith\","
                                        + "\"email\":\"jane.smith@example.com\","
                                        + "\"phoneNumber\":\"0987654321\","
                                        + "\"resume\":\"Expert in surgery\","
                                        + "\"workday\":["
                                        + "\"Tuesday\", \"Thursday\""
                                        + "],"
                                        + "\"workHoursJson\":\"09:00-17:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"surgery\","
                                        + "\"name\":\"Surgery\""
                                        + "}"
                                        + "],"
                                        + "\"photoDefault\":true"
                                        + "}"
                                        + "]"))
                );
    }

    public void registerGetVetsEndpoint_withNoVets() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/no_vets")
                )
                .respond(
                        response()
                                .withStatusCode(404)
                );
    }

    public void registerAddVetEndpoint() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/vets")
                                .withBody(json("{"
                                        + "\"vetBillId\":\"bill001\","
                                        + "\"firstName\":\"John\","
                                        + "\"lastName\":\"Doe\","
                                        + "\"email\":\"john.doe@example.com\","
                                        + "\"phoneNumber\":\"1234567890\","
                                        + "\"resume\":\"Specialist in dermatology\","
                                        + "\"workday\":["
                                        + "\"Wednesday\""
                                        + "],"
                                        + "\"workHoursJson\":\"08:00-16:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"dermatology\","
                                        + "\"name\":\"Dermatology\""
                                        + "}"
                                        + "],"
                                        + "\"photoDefault\":false"
                                        + "}"))
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody(json("{"
                                        + "\"vetId\":\"2e26e7a2-8c6e-4e2d-8d60-ad0882e295eb\","
                                        + "\"vetBillId\":\"bill001\","
                                        + "\"firstName\":\"John\","
                                        + "\"lastName\":\"Doe\","
                                        + "\"email\":\"john.doe@example.com\","
                                        + "\"phoneNumber\":\"1234567890\","
                                        + "\"resume\":\"Specialist in dermatology\","
                                        + "\"workday\":["
                                        + "\"Wednesday\""
                                        + "],"
                                        + "\"workHoursJson\":\"08:00-16:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"dermatology\","
                                        + "\"name\":\"Dermatology\""
                                        + "}"
                                        + "]"
                                        + "}"))
                );
    }


    public void registerUpdateVetEndpoint() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("PUT")
                                .withPath("/vets/c02cbf82-625b-11ee-8c99-0242ac120002")
                                .withBody(json("{"
                                        + "\"vetId\":\"c02cbf82-625b-11ee-8c99-0242ac120002\","
                                        + "\"vetBillId\":\"bill001\","
                                        + "\"firstName\":\"John\","
                                        + "\"lastName\":\"Doe\","
                                        + "\"email\":\"john.doe@example.com\","
                                        + "\"phoneNumber\":\"1234567890\","
                                        + "\"resume\":\"Specialist in dermatology\","
                                        + "\"workday\":["
                                        + "\"Wednesday\""
                                        + "],"
                                        + "\"workHoursJson\":\"08:00-16:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"dermatology\","
                                        + "\"name\":\"Dermatology\""
                                        + "}"
                                        + "]"
                                        + "}"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json("{"
                                        + "\"vetId\":\"c02cbf82-625b-11ee-8c99-0242ac120002\","
                                        + "\"vetBillId\":\"bill001\","
                                        + "\"firstName\":\"John\","
                                        + "\"lastName\":\"Doe\","
                                        + "\"email\":\"john.doe@example.com\","
                                        + "\"phoneNumber\":\"1234567890\","
                                        + "\"resume\":\"Specialist in dermatology\","
                                        + "\"workday\":["
                                        + "\"Wednesday\""
                                        + "],"
                                        + "\"workHoursJson\":\"08:00-16:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"dermatology\","
                                        + "\"name\":\"Dermatology\""
                                        + "}"
                                        + "]"
                                        + "}"))
                );
    }

    public void registerUpdateVetEndpoint_withInvalidId() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("PUT")
                                .withPath("/vets/c02cbf82-625b-11ee-8c99-0242ac120002") // The vetId to update
                                .withBody(json("{"
                                        + "\"vetId\":\"invalid-vet-id\","
                                        + "\"vetBillId\":\"bill001\","
                                        + "\"firstName\":\"John\","
                                        + "\"lastName\":\"Doe\","
                                        + "\"email\":\"john.doe@example.com\","
                                        + "\"phoneNumber\":\"1234567890\","
                                        + "\"resume\":\"Specialist in dermatology\","
                                        + "\"workday\":["
                                        + "\"Wednesday\""
                                        + "],"
                                        + "\"workHoursJson\":\"08:00-16:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"dermatology\","
                                        + "\"name\":\"Dermatology\""
                                        + "}"
                                        + "]"
                                        + "}"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json("{"
                                        + "\"vetId\":\"invalid-vet-id\","
                                        + "\"vetBillId\":\"bill001\","
                                        + "\"firstName\":\"John\","
                                        + "\"lastName\":\"Doe\","
                                        + "\"email\":\"john.doe@example.com\","
                                        + "\"phoneNumber\":\"1234567890\","
                                        + "\"resume\":\"Specialist in dermatology\","
                                        + "\"workday\":["
                                        + "\"Wednesday\""
                                        + "],"
                                        + "\"workHoursJson\":\"08:00-16:00\","
                                        + "\"active\":true,"
                                        + "\"specialties\":["
                                        + "{"
                                        + "\"specialtyId\":\"dermatology\","
                                        + "\"name\":\"Dermatology\""
                                        + "}"
                                        + "]"
                                        + "}"))
                );
    }

public void stopMockServer() {
        if(clientAndServer != null)
            this.clientAndServer.stop();
    }
    public void registerGetVetByFirstNameEndpoint(String firstName, VetResponseDTO responseDTO) throws JsonProcessingException {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/vets/firstName/" + firstName)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json(new ObjectMapper().writeValueAsString(responseDTO)))
                );
    }

    public void registerGetVetByFirstNameEndpointNotFound(String firstName) {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/vets/firstName/" + firstName)
                )
                .respond(
                        response()
                                .withStatusCode(404)
                );
    }

    public void registerGetVetByLastNameEndpoint(String lastName, VetResponseDTO responseDTO) throws JsonProcessingException {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/vets/lastName/" + lastName)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json(new ObjectMapper().writeValueAsString(responseDTO)))
                );
    }

    public void registerGetVetByLastNameEndpointNotFound(String lastName) {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/vets/lastName/" + lastName)
                )
                .respond(
                        response()
                                .withStatusCode(404)
                );
    }

    public void registerGetVetByIdEndpoint() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/vets/ac9adeb8-625b-11ee-8c99-0242ac120002")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json(new VetResponseDTO(
                                        "ac9adeb8-625b-11ee-8c99-0242ac120002",
                                        "5",
                                        "Henry",
                                        "Stevens",
                                        "stevenshenry@email.com",
                                        "(514)-634-8276 #2389",
                                        "Practicing since 1 years",
                                        Set.of(Workday.Wednesday, Workday.Tuesday, Workday.Thursday, Workday.Monday),
                                        "{\"Thursday\":[\"Hour_8_9\",\"Hour_9_10\",\"Hour_10_11\",\"Hour_11_12\"],"
                                                + "\"Monday\":[\"Hour_8_9\",\"Hour_9_10\",\"Hour_10_11\",\"Hour_11_12\",\"Hour_12_13\",\"Hour_13_14\",\"Hour_14_15\",\"Hour_15_16\"],"
                                                + "\"Wednesday\":[\"Hour_10_11\",\"Hour_11_12\",\"Hour_12_13\",\"Hour_13_14\",\"Hour_14_15\",\"Hour_15_16\",\"Hour_16_17\",\"Hour_17_18\"],"
                                                + "\"Tuesday\":[\"Hour_12_13\",\"Hour_13_14\",\"Hour_14_15\",\"Hour_15_16\",\"Hour_16_17\",\"Hour_17_18\",\"Hour_18_19\",\"Hour_19_20\"]}",
                                        false,
                                        Set.of(
                                                new SpecialtyDTO("surgery", "surgery"),
                                                new SpecialtyDTO("radiology", "radiology")
                                        )
                                ))));
    }

    public void registerGetVetByInvalidIdEndpoint() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/vets/ac9adeb8-625b-11ee-8c99-0242ac12000200000")
                )
                .respond(
                        response()
                                .withStatusCode(404)
                                .withBody("{\"statusCode\":404,\"message\":\"vetId not found: invalid-id\",\"timestamp\":\"" + java.time.Instant.now() + "\"}")
                );
    }


    public void registerDeleteVetEndpoint() {
        mockServerClient_VetService
                .when(
                        request()
                                .withMethod("DELETE")
                                .withPath("/vets/[a-f0-9\\-]+")
                )
                .respond(
                        response()
                                .withStatusCode(204)
                );
    }

}
