package entities;

class Inventory {
    public final int id;
    public String name;
    public int amt;
    public int restock_amt;

    public Inventory(int id, String name, int amt, int restock_amt) {
        this.id = id;
        this.name = name;
        this.amt = amt;
        this.restock_amt = restock_amt;
    }
}
