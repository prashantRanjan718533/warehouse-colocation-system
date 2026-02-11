package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyStoreManagerGatewayTest {

    @Test
    void shouldCreateStoreOnLegacySystem() {

        LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();

        Store store = new Store();
        store.name = "TEST_STORE";
        store.quantityProductsInStock = 50;

        // Should not throw exception
        assertDoesNotThrow(() ->
                gateway.createStoreOnLegacySystem(store)
        );
    }

    @Test
    void shouldUpdateStoreOnLegacySystem() {

        LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();

        Store store = new Store();
        store.name = "UPDATE_STORE";
        store.quantityProductsInStock = 100;

        // Should not throw exception
        assertDoesNotThrow(() ->
                gateway.updateStoreOnLegacySystem(store)
        );
    }
}
