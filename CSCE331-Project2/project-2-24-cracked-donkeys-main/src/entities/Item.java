package entities;

/**
 * The Item class represents a product or item that can be sold during a sale.
 * Items combine together to create Sellables, for example a serving of orange
 * chicken combines with a serving of fried rice to create a bowl.
 */
public class Item {
    public final int id; // -1 if not yet stored in the database
    public String name;
    public double additionalPrice;

    public Item(int id, String name, double additionalPrice) {
        this.id = id;
        this.name = name;
        this.additionalPrice = additionalPrice;
    }
}
