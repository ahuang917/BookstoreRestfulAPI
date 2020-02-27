package business.order;

import java.time.LocalDateTime;

public class Order {

    private long orderId;
    private int amount;
    private LocalDateTime dateCreated;
    private long confirmationNumber;
    private long customerId;

    public Order(long orderId, int amount, LocalDateTime dateCreated, long confirmationNumber, long customerId) {
        this.orderId = orderId;
        this.amount = amount;
        this.dateCreated = dateCreated;
        this.confirmationNumber = confirmationNumber;
        this.customerId = customerId;
    }

    public long getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public long getConfirmationNumber() {
        return confirmationNumber;
    }

    public long getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", amount=" + amount +
                ", dateCreated=" + dateCreated +
                ", confirmationNumber=" + confirmationNumber +
                ", customerId=" + customerId +
                '}';
    }
}
