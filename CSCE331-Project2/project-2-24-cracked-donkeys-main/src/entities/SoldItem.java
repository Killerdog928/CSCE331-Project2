package entities;

import entities.Item;

class SoldItem {
    public final int id;
    public int itemId;
    public int soldSellableId;
    public double amount;

    public SoldItem(int id, int itemId, int soldSellableId, double amount) {
        this.id = id;
        this.itemId = itemId;
        this.soldSellableId = soldSellableId;
        this.amount = amount;
    }
}
