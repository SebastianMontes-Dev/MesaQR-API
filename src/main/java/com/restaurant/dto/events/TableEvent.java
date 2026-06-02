package com.restaurant.dto.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OrderUpdateEvent.class, name = "ORDER_UPDATE"),
    @JsonSubTypes.Type(value = TableStatusEvent.class, name = "STATUS_CHANGE")
})
public abstract class TableEvent {
    private Long tableId;
    private Integer tableNumber;

    protected TableEvent(Long tableId, Integer tableNumber) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
    }
}
