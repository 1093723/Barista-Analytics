package Model;

import java.util.Calendar;

public class CoffeeOrder {
    private String uuid;
    private String order_Description;
    private String order_CustomerUsername;
    private Long order_Total;
    private String order_date;
    private String order_State;

    public String getOrder_Store() {
        return order_Store;
    }

    public void setOrder_Store(String order_Store) {
        this.order_Store = order_Store;
    }

    private String order_Store;


    public CoffeeOrder(String UUID, String order_Description,
                       String order_CustomerUsername, Long order_Total, String order_date) {
        this.uuid = UUID;
        this.order_Description = order_Description;
        this.order_CustomerUsername = order_CustomerUsername;
        this.order_Total = order_Total;
        this.order_date = order_date;
        this.order_State = "Confirmed";
    }

    public CoffeeOrder() {
        this.order_State = "Requested";
        this.order_date =java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_State() {
        return order_State;
    }

    public void setOrder_State(String order_State) {
        this.order_State = order_State;
    }
    public void setOrder_Total(Long order_Total) {
        this.order_Total = order_Total;
    }


    public String getUUID() {
        return uuid;
    }

    public void setUUID(String UUID) {
        this.uuid = UUID;
    }

    public String getOrder_Description() {
        return order_Description;
    }

    public Long getOrder_Total() { return order_Total; }

    public void setOrder_Description(String order_Description) {
        this.order_Description = order_Description;
    }


    public String getOrder_CustomerUsername() {
        return order_CustomerUsername;
    }

    public void setOrder_CustomerUsername(String order_CustomerUsername) {
        this.order_CustomerUsername = order_CustomerUsername;
    }
}
