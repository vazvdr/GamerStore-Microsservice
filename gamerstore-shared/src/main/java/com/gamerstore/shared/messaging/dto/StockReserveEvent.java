package com.gamerstore.shared.messaging.dto;

import java.io.Serializable;
import java.util.List;

public class StockReserveEvent implements Serializable {

    private Long userId;
    private List<ItemDTO> items;

    public StockReserveEvent() {}

    public StockReserveEvent(Long userId, List<ItemDTO> items) {
        this.userId = userId;
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
}