package com.restaurant.dto.events;

import com.restaurant.model.TableStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TableStatusEvent extends TableEvent {
    private TableStatus newStatus;

    public TableStatusEvent() {
        super();
    }

    public TableStatusEvent(Long tableId, Integer tableNumber, TableStatus newStatus) {
        super(tableId, tableNumber);
        this.newStatus = newStatus;
    }
}
