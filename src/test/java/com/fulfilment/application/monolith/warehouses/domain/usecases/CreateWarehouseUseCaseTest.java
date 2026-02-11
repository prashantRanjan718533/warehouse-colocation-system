package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateWarehouseUseCaseTest {

    private WarehouseRepository warehouseRepository;
    private LocationResolver locationResolver;
    private CreateWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        locationResolver = mock(LocationResolver.class);
        useCase = new CreateWarehouseUseCase(warehouseRepository, locationResolver);
    }

    private Warehouse buildWarehouse() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "MWH.001";
        w.location = "AMSTERDAM-001";
        w.capacity = 100;
        w.stock = 50;
        return w;
    }

    private Location buildLocation() {
        Location location = new Location("AMSTERDAM-001", 1000, 5);

        return location;
    }

    // =========================
    // ✅ POSITIVE CASE
    // =========================
//    @Test
//    void shouldCreateWarehouseSuccessfully() {
//
//        Warehouse warehouse = buildWarehouse();
//        Location location = buildLocation();
//
//        when(warehouseRepository.findByBusinessUnitCode("MWH.001")).thenReturn(null);
//        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
//        when(warehouseRepository.countByLocation("AMSTERDAM-001")).thenReturn(1L);
//        when(warehouseRepository.totalCapacityByLocation("AMSTERDAM-001")).thenReturn(200);
//
//        useCase.create(warehouse);
//
//        verify(warehouseRepository).create(warehouse);
//    }
    @Test
    void shouldFailWhenMaxWarehousesReached() {

        Warehouse warehouse = buildWarehouse();
        Location location = buildLocation();

        location.maxNumberOfWarehouses = 5;
        location.maxCapacity = 1000;   // ensure capacity won't fail

        when(warehouseRepository.findByBusinessUnitCode("MWH.001"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
                .thenReturn(location);

        when(warehouseRepository.countByLocation("AMSTERDAM-001"))
                .thenReturn(5L); // equal to max

        // IMPORTANT: mock capacity so branch 3 does not fire
        when(warehouseRepository.totalCapacityByLocation("AMSTERDAM-001"))
                .thenReturn(100); // safe value

        assertThrows(IllegalStateException.class,
                () -> useCase.create(warehouse));
    }

    // =========================
    // ❌ DUPLICATE BUSINESS CODE
    // =========================
    @Test
    void shouldFailWhenBusinessUnitAlreadyExists() {

        Warehouse warehouse = buildWarehouse();

        when(warehouseRepository.findByBusinessUnitCode("MWH.001"))
                .thenReturn(new Warehouse());

        assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));
    }

    // =========================
    // ❌ MAX WAREHOUSE COUNT
    // =========================
//    @Test
//    void shouldFailWhenMaxWarehousesReached() {
//
//        Warehouse warehouse = buildWarehouse();
//        Location location = buildLocation();
//
//        when(warehouseRepository.findByBusinessUnitCode("MWH.001")).thenReturn(null);
//        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
//        when(warehouseRepository.countByLocation("AMSTERDAM-001"))
//                .thenReturn(5L); // equal to maxNumberOfWarehouses
//
//        assertThrows(IllegalStateException.class,
//                () -> useCase.create(warehouse));
//    }

    @Test
    void shouldCreateWarehouseSuccessfully() {

        Warehouse warehouse = buildWarehouse();
        Location location = buildLocation();

        location.maxCapacity = 1000;
        location.maxNumberOfWarehouses = 5;

        when(warehouseRepository.findByBusinessUnitCode("MWH.001"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
                .thenReturn(location);

        when(warehouseRepository.countByLocation("AMSTERDAM-001"))
                .thenReturn(1L);

        when(warehouseRepository.totalCapacityByLocation("AMSTERDAM-001"))
                .thenReturn(200); // 200 + 100 < 1000

        useCase.create(warehouse);

        verify(warehouseRepository).create(warehouse);
    }


    // =========================
    // ❌ LOCATION CAPACITY EXCEEDED
    // =========================
    @Test
    void shouldFailWhenLocationCapacityExceeded() {

        Warehouse warehouse = buildWarehouse();
        Location location = buildLocation();

        when(warehouseRepository.findByBusinessUnitCode("MWH.001")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
        when(warehouseRepository.countByLocation("AMSTERDAM-001")).thenReturn(1L);
        when(warehouseRepository.totalCapacityByLocation("AMSTERDAM-001"))
                .thenReturn(950); // 950 + 100 > 1000

        assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));
    }

    // =========================
    // ❌ STOCK GREATER THAN CAPACITY
    // =========================
    @Test
    void shouldFailWhenStockExceedsCapacity() {

        Warehouse warehouse = buildWarehouse();
        warehouse.stock = 200; // greater than capacity

        Location location = buildLocation();

        when(warehouseRepository.findByBusinessUnitCode("MWH.001")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
        when(warehouseRepository.countByLocation("AMSTERDAM-001")).thenReturn(1L);
        when(warehouseRepository.totalCapacityByLocation("AMSTERDAM-001")).thenReturn(200);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));
    }
}
