package com.petclinic.bffapigateway.presentationlayer.v2.mockservers;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class MockServerConfigBillService {

    private static final Integer BILL_SERVICE_SERVER_PORT = 7004;
    private final ClientAndServer clientAndServer;
    private final MockServerClient mockServerClient_BillService = new MockServerClient("localhost", BILL_SERVICE_SERVER_PORT);
    public MockServerConfigBillService() {
        this.clientAndServer = ClientAndServer.startClientAndServer(BILL_SERVICE_SERVER_PORT);
    }

    public void registerGetAllBillsEndpoint() {

        String response = "["
                + "{\"billId\":\"e6c7398e-8ac4-4e10-9ee0-03ef33f0361b\",\"customerId\":\"e6c7398e-8ac4-4e10-9ee0-03ef33f0361a\",\"visitType\":\"general\",\"vetId\":\"2\",\"date\":\"2024-10-11\",\"amount\":\"120\",\"taxedAmount\":\"0.0\", \"billStatus\":\"UNPAID\", \"dueDate\":\"2024-10-13\"},"
                + "{\"billId\":\"e6c7398e-8ac4-4e10-9ee0-03ef33f0361a\",\"customerId\":\"e6c7398e-8ac4-4e10-9ee0-03ef33f0361a\",\"visitType\":\"general\",\"vetId\":\"2\",\"date\":\"2024-10-11\",\"amount\":\"100\",\"taxedAmount\":\"10.0\", \"billStatus\":\"UNPAID\", \"dueDate\":\"2024-10-13\"}"
                + "]";

        mockServerClient_BillService
                .when(
                    request()
                            .withMethod("GET")
                            .withPath("/bills")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json(response))
                );
    }

    public void stopMockServer() {
        if(clientAndServer != null)
            this.clientAndServer.stop();
    }
}
