package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

    @Override
    public List<Warehouse> getAll() {
        return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
    }

    @Override
    public void create(Warehouse warehouse) {
        DbWarehouse db = new DbWarehouse();
        db.businessUnitCode = warehouse.businessUnitCode;
        db.location = warehouse.location;
        db.capacity = warehouse.capacity;
        db.stock = warehouse.stock;
        db.createdAt = LocalDateTime.now();
        persist(db);
    }

    @Override
    public void update(Warehouse warehouse) {
        DbWarehouse db =
                find("businessUnitCode = ?1 and archivedAt is null",
                        warehouse.businessUnitCode)
                        .firstResult();

        if (db == null) {
            throw new IllegalStateException("Active warehouse not found");
        }

        db.capacity = warehouse.capacity;
        db.stock = warehouse.stock;
        db.archivedAt = warehouse.archivedAt;
    }

    @Override
    public void remove(Warehouse warehouse) {
        delete("businessUnitCode", warehouse.businessUnitCode);
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
        DbWarehouse db =
                find("businessUnitCode = ?1 and archivedAt is null", buCode)
                        .firstResult();

        return db == null ? null : db.toWarehouse();
    }
    public long countByLocation(String location) {
        return count("location = ?1 and archivedAt is null", location);
    }

    public int totalCapacityByLocation(String location) {
        return list("location = ?1 and archivedAt is null", location)
                .stream()
                .mapToInt(w -> w.capacity)
                .sum();
    }
}
