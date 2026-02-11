package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    // TODO implement this method
      Warehouse old =
              warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

      if (old == null) {
          throw new IllegalStateException("Warehouse to replace not found");
      }
      if (newWarehouse.capacity < old.stock) {
          throw new IllegalArgumentException("New capacity cannot hold old stock");
      }
      if (!newWarehouse.stock.equals(old.stock)) {
          throw new IllegalArgumentException("Stock must match previous warehouse");
      }
      old.archivedAt = LocalDateTime.now();
      warehouseStore.update(old);

      newWarehouse.createdAt = LocalDateTime.now();
      warehouseStore.create(newWarehouse);
  }
}
