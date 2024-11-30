package data_classes;

import java.util.List;

public class SellableEdit {

    /**
     * The price of the sellable product.
     */
    public double price;

    public int sellableType;
    public int numEntrees;
    public int numSides;
    public int numAppetizers;
    public String name;
    public SellableEdit( double price, String name, int sellableType, int numEntrees, int numSides, int numAppetizers) {
        this.name = name;
        this.price = price;
        this.sellableType = sellableType;
        this.numEntrees = numEntrees;
        this.numSides = numSides;
        this.numAppetizers = numAppetizers;
    }
}
