package entities;

public class SoldSellable {
    public final int id; // -1 if not yet stored in the database
    public int saleId;
    public int sellableId;

    public SoldSellable(int id, int saleId, int sellableId) {
        this.id = id;
        this.saleId = saleId;
        this.sellableId = sellableId;
    }
}
