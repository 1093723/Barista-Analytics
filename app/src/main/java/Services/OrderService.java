package Services;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import Model.CoffeeOrder;

public class OrderService {
    public String OrderStore;
    public String userName;
    public String OrderDate;
    public int Order_Total;
    public String OrderDescription;

    public OrderService() {
        OrderDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    }

    public int getOrder_Total() {
        return Order_Total;
    }

    public Boolean processOrder(String OrderStore, String userName,String Uid, String quantity, String nameOfCoffee, Long beverage_price, DatabaseReference databaseOrder){
        Boolean flag = false;
        try {
            //String id = databaseOrder.push().getKey();
            //int int_id = Integer.parseInt(id);
            this.OrderDescription = quantity + " x " + nameOfCoffee + " on " + OrderDate;
            CoffeeOrder order = new CoffeeOrder(Uid, OrderDescription, OrderStore, userName, beverage_price);

            if(databaseOrder.child(userName).setValue(order).isSuccessful()){
                flag = true;
            }
        }catch (NullPointerException e){
            flag = true;
        }
        return flag;

    }
}
