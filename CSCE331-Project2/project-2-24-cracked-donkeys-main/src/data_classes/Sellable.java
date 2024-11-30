package data_classes;

import java.util.List;

/**
 * The Sellable class represents a product or item that can be sold during a sale.
 * Each Sellable object has an ID, a price, and a list of associated items.
 */
public class Sellable {

    /**
     * The unique identifier for the sellable product.
     */
    public int sellableId;

    /**
     * The list of items associated with the sellable product.
     */
    public List<Item> items;

    /**
     * The price of the sellable product.
     */
    public double price;

    /**
     * Constructs a Sellable with the specified sellable ID, price, and list of items.
     *
     * @param sellableId the unique identifier for the sellable product
     * @param price the price of the sellable product
     * @param items the list of items associated with the sellable product
     */
    public Sellable(int sellableId, double price, List<Item> items) {
        this.sellableId = sellableId;
        this.items = items;
        this.price = price;
    }
}
