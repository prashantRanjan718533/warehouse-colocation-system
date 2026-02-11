package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public CreateWarehouseUseCase(
            WarehouseStore warehouseStore,
            LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void create(Warehouse warehouse) {
        // TODO implement this method
        if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
            throw new IllegalArgumentException("Business Unit Code already exists");
        }
        Location location = locationResolver.resolveByIdentifier(warehouse.location);
        long count =
                ((WarehouseRepository) warehouseStore)
                        .countByLocation(location.identification);

        if (count >= location.maxNumberOfWarehouses) {
            throw new IllegalStateException("Maximum warehouses reached for location");
        }
        int usedCapacity =
                ((WarehouseRepository) warehouseStore)
                        .totalCapacityByLocation(location.identification);

        if (usedCapacity + warehouse.capacity > location.maxCapacity) {
            throw new IllegalArgumentException("Location capacity exceeded");
        }
        if (warehouse.stock > warehouse.capacity) {
            throw new IllegalArgumentException("Stock exceeds warehouse capacity");
        }
        warehouseStore.create(warehouse);
    }
}
