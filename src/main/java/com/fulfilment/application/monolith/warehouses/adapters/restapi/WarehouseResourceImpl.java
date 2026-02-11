package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject
    private WarehouseRepository warehouseRepository;

    @Inject
    CreateWarehouseOperation createWarehouse;
    @Inject
    ReplaceWarehouseOperation replaceWarehouse;
    @Inject
    ArchiveWarehouseOperation archiveWarehouse;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
    }

    @Override
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createANewWarehouseUnit'");
    }

    @Override
    public Warehouse getAWarehouseUnitByID(String id) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found");
        }
        return toWarehouseResponse(warehouse);
    }

    @Override
    public void archiveAWarehouseUnitByID(String id) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found");
        }
        archiveWarehouse.archive(warehouse);
    }

    @Override
    public Warehouse replaceTheCurrentActiveWarehouse(
            String businessUnitCode, @NotNull Warehouse data) {
        // TODO Auto-generated method stub
        data.setBusinessUnitCode(businessUnitCode);
        replaceWarehouse.replace(toDomain(data));
        return data;
    }

    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
        var response = new Warehouse();
        response.setBusinessUnitCode(warehouse.businessUnitCode);
        response.setLocation(warehouse.location);
        response.setCapacity(warehouse.capacity);
        response.setStock(warehouse.stock);

        return response;
    }

    private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse
    toDomain(Warehouse api) {
        var w = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        w.businessUnitCode = api.getBusinessUnitCode();
        w.location = api.getLocation();
        w.capacity = api.getCapacity();
        w.stock = api.getStock();
        return w;
    }
}
