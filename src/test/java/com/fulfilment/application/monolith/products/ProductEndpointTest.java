package com.fulfilment.application.monolith.products;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductUsingNoArgsConstructor() {

        Product product = new Product();

        product.name = "TABLE";
        product.description = "Wooden table";
        product.price = new BigDecimal("199.99");
        product.stock = 10;

        assertEquals("TABLE", product.name);
        assertEquals("Wooden table", product.description);
        assertEquals(new BigDecimal("199.99"), product.price);
        assertEquals(10, product.stock);
    }

    @Test
    void shouldCreateProductUsingNameConstructor() {

        Product product = new Product("CHAIR");

        assertEquals("CHAIR", product.name);
        assertNull(product.description);
        assertNull(product.price);
        assertEquals(0, product.stock);
    }
}
