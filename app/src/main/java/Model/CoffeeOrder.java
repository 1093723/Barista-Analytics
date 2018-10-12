package Model;

public class CoffeeOrder {
    private String UUID;
    private String order_Description;
    private String order_Store;
    private String order_CustomerUsername;
    private Long Order_Total;

    public CoffeeOrder() {
    }

    public CoffeeOrder(String uID, String order_Description, String order_Store,
                       String order_CustomerUsername, Long Order_total) {
        this.UUID = UUID;
        this.Order_Total = Order_total;
        this.order_Description = order_Description;
        this.order_Store = order_Store;
        this.order_CustomerUsername = order_CustomerUsername;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getOrder_Description() {
        return order_Description;
    }

    public Long getOrder_Total() { return Order_Total; }

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
