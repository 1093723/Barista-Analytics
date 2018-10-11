package Model;

public class CoffeeOrder {
    private int orderID;
    private String order_Description;
    private String order_Store;
    private String order_CustomerUsername;
    private int Order_Total;

    public CoffeeOrder() {
    }

    public CoffeeOrder(int orderID, String order_Description, String order_Store,
                       String order_CustomerUsername, int Order_total) {
        this.orderID = orderID;
        this.Order_Total = Order_total;
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

    public int getOrder_Total() { return Order_Total; }

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
