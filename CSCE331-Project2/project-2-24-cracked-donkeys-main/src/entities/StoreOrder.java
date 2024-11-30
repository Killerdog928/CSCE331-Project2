package entities;

import java.sql.Date;

class StoreOrder {
    public final int id; // -1 if not yet stored in the database
    public String itemName;
    public double price;
    public Date expDate;
    public Date orderDate;
    public int quantity;

    public StoreOrder(int id, String itemName, double price, Date expDate, Date orderDate, int quantity) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.expDate = expDate;
        this.orderDate = orderDate;
        this.quantity = quantity;
    }
}
