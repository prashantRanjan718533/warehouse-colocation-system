package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class WarehouseEndpointIT {

    private static final String PATH = "warehouse";

    // ----------------------------------------------------
    // ✅ POSITIVE: list all warehouses
    // ----------------------------------------------------

    @Test
    void shouldListAllWarehouses() {
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(
                        containsString("MWH.001"),
                        containsString("MWH.012"),
                        containsString("MWH.023"));
    }

    // ----------------------------------------------------
    // ❌ NEGATIVE 1: get non-existing warehouse
    // ----------------------------------------------------

    @Test
    void shouldReturn404WhenWarehouseDoesNotExist() {
        given()
                .when()
                .get(PATH + "/NON_EXISTENT_BU")
                .then()
                .statusCode(404);
    }

    // ----------------------------------------------------
    // ❌ NEGATIVE 2: archive non-existing warehouse
    // ----------------------------------------------------

    @Test
    void shouldReturn404WhenArchivingNonExistingWarehouse() {
        given()
                .when()
                .delete(PATH + "/NON_EXISTENT_BU")
                .then()
                .statusCode(404);
    }

    // ----------------------------------------------------
    // ❌ NEGATIVE 3: archived warehouse should not appear in list
    // ----------------------------------------------------

    @Test
    void shouldNotReturnArchivedWarehouse() {
        // archive existing warehouse by businessUnitCode
        given()
                .when()
                .delete(PATH + "/MWH.001")
                .then()
                .statusCode(204);

        // verify it no longer appears in list
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(
                        not(containsString("MWH.001")),
                        containsString("MWH.012"),
                        containsString("MWH.023"));
    }
}
