package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReplaceWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseStore = Mockito.mock(WarehouseStore.class);
        useCase = new ReplaceWarehouseUseCase(warehouseStore);
    }

    private Warehouse buildOldWarehouse() {
        Warehouse old = new Warehouse();
        old.businessUnitCode = "MWH.001";
        old.capacity = 200;
        old.stock = 100;
        return old;
    }

    private Warehouse buildNewWarehouse() {
        Warehouse nw = new Warehouse();
        nw.businessUnitCode = "MWH.001";
        nw.capacity = 200;
        nw.stock = 100;
        return nw;
    }

    // =============================
    // 1️⃣ Warehouse not found
    // =============================
    @Test
    void shouldFailWhenWarehouseNotFound() {

        Warehouse newWarehouse = buildNewWarehouse();

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> useCase.replace(newWarehouse));
    }

    // =============================
    // 2️⃣ Capacity too small
    // =============================
    @Test
    void shouldFailWhenCapacityTooSmall() {

        Warehouse old = buildOldWarehouse();
        Warehouse newWarehouse = buildNewWarehouse();
        newWarehouse.capacity = 50; // less than old.stock (100)

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(old);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));
    }

    // =============================
    // 3️⃣ Stock mismatch
    // =============================
    @Test
    void shouldFailWhenStockDoesNotMatch() {

        Warehouse old = buildOldWarehouse();
        Warehouse newWarehouse = buildNewWarehouse();
        newWarehouse.stock = 90; // mismatch

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(old);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));
    }

    // =============================
    // 4️⃣ Successful replacement
    // =============================
    @Test
    void shouldReplaceWarehouseSuccessfully() {

        Warehouse old = buildOldWarehouse();
        Warehouse newWarehouse = buildNewWarehouse();

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(old);

        useCase.replace(newWarehouse);

        // verify archive
        assertNotNull(old.archivedAt);

        // verify creation timestamp
        assertNotNull(newWarehouse.createdAt);

        verify(warehouseStore).update(old);
        verify(warehouseStore).create(newWarehouse);
    }
}
