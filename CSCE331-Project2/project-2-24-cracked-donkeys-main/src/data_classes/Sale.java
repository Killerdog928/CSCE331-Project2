package data_classes;

import java.sql.Timestamp;
import java.util.List;

/**
 * The Sale class represents a transaction where multiple sellable products are purchased.
 * It stores details such as the employee responsible for the sale, the total price, 
 * the date and time of the sale, and the list of sellable products included in the sale.
 */
public class Sale {

    /**
     * The ID of the employee responsible for the sale.
     */
    public int employeeId;

    /**
     * The total price of the sale.
     */
    public double totalPrice;

    /**
     * The date and time when the sale occurred.
     */
    public Timestamp orderDate;

    /**
     * The list of sellable products included in the sale.
     */
    public List<Sellable> sellables;

    /**
     * Constructs a Sale with the specified employee ID, total price, order date, 
     * and list of sellable products.
     *
     * @param employeeId the ID of the employee responsible for the sale
     * @param totalPrice the total price of the sale
     * @param orderDate the date and time when the sale occurred
     * @param sellables the list of sellable products included in the sale
     */
    public Sale(int employeeId, double totalPrice, Timestamp orderDate, List<Sellable> sellables) {
        this.employeeId = employeeId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.sellables = sellables;
    }
}
