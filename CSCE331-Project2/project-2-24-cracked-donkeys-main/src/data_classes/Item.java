package data_classes;


public class Item {
    public int itemId;

    /**
     * The additional price for the item.
     */
    public int addPrice;

    /**
     * The amount associated with the item.
     */
    public double amount;

    /**
     * Constructs an Item with the specified item ID, additional price, and amount.
     *
     * @param itemId the unique identifier for the item
     * @param addPrice the additional price for the item
     * @param amount the amount associated with the item
     */
    public Item(int itemId, int addPrice, double amount) {
        this.itemId = itemId;
        this.addPrice = addPrice;
        this.amount = amount;
    }
}
