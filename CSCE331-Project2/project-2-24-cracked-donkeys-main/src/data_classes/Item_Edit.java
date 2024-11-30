package data_classes;

/**
 * The Item class represents a product or item that can be edited in the item database
 */
public class Item_Edit {
    public String name;
    public int item_type; // 0 for entree, 1 for drink, 2 for appetizer
    public double additionalPrice;

    public Item_Edit(String name, int item_type, double additionalPrice) {
        this.name = name;
        this.item_type = item_type;
        this.additionalPrice = additionalPrice;
    }
}
