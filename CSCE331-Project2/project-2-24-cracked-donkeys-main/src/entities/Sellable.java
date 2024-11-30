package entities;

/**
 * The Sellable class represents a product or item that can be sold during a
 * sale.
 * Each Sellable object has an ID, a price, and a list of associated items.
 */
class Sellable {
    public final int id; // -1 if not yet stored in the database
    public String name;
    public double price;
    public boolean isActive;

    public Sellable(int id, String name, double price, boolean isActive) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isActive = isActive;
    }
}
