package Order;

public class CoffeeOrder {
    private int orderID;
    private String order_Description;
    private String order_Store;
    private String order_CustomerUsername;

    public CoffeeOrder() {
    }

    public CoffeeOrder(int orderID, String order_Description, String order_Store,
                       String order_CustomerUsername) {
        this.orderID = orderID;
        this.order_Description = order_Description;
        this.order_Store = order_Store;
        this.order_CustomerUsername = order_CustomerUsername;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrder_Description() {
        return order_Description;
    }

    public void setOrder_Description(String order_Description) {
        this.order_Description = order_Description;
    }

    public String getOrder_Store() {
        return order_Store;
    }

    public void setOrder_Store(String order_Store) {
        this.order_Store = order_Store;
    }

    public String getOrder_CustomerUsername() {
        return order_CustomerUsername;
    }

    public void setOrder_CustomerUsername(String order_CustomerUsername) {
        this.order_CustomerUsername = order_CustomerUsername;
    }
}
