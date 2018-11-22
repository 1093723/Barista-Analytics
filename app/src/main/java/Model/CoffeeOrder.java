package Model;

import java.util.Calendar;

public class CoffeeOrder {

    private Integer order_ID;
    private String uuid;
    private String order_Description;
    private String order_CustomerUsername;
    private Long order_Total;
    private String order_Date;
    private String order_State;
    private Float order_Rating;
    private String order_Store;



    public CoffeeOrder(Integer order_ID, String uuid, String order_Description,
                       String order_CustomerUsername, Long order_Total, String order_Date,
                       String order_State, Float order_Rating, String order_Store) {
        this.order_ID = order_ID;
        this.uuid = uuid;
        this.order_Description = order_Description;
        this.order_CustomerUsername = order_CustomerUsername;
        this.order_Total = order_Total;
        this.order_Date = order_Date;
        this.order_State = order_State;
        this.order_Rating = order_Rating;
        this.order_Store = order_Store;
    }

    public CoffeeOrder() {
        this.order_State = "Ordered";
        this.order_Date =java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        order_Rating = Float.valueOf("0");
    }

    public String getOrder_Store() {
        return order_Store;
    }
    public void setOrder_Store(String order_Store) {
        this.order_Store = order_Store;
    }
    public String getOrder_Date() {
        return order_Date;
    }

    public void setOrder_Date(String order_Date) {
        this.order_Date = order_Date;
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

    public Float getOrder_Rating() {
        return order_Rating;
    }

    public void setOrder_Rating(Float order_Rating) {
        this.order_Rating = order_Rating;
    }

    public Integer getOrder_ID() {
        return order_ID;
    }

    public void setOrder_ID(Integer order_ID) {
        this.order_ID = order_ID;
    }
}
