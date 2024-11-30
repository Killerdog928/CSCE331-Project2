package entities;

import java.sql.Timestamp;
import java.util.List;

/**
 * The Sale class represents a transaction where multiple sellable products are
 * purchased.
 * It stores details such as the employee responsible for the sale, the total
 * price, the date and time of the sale,
 * and the list of sellable products included in the sale.
 */
class Sale {
    public final int id; // -1 if not yet stored in the database
    public int employeeId;
    public double totalPrice;
    public Timestamp orderDate;

    public Sale(int id, int employeeId, double totalPrice, Timestamp orderDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }
}
