package Services;

import com.google.firebase.database.DatabaseReference;

import org.joda.time.DateTime;

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
    public boolean process_order(CoffeeOrder order,DatabaseReference reference){
        Boolean flag = false;
        String id = reference.push().getKey();
        order.setOrder_date(DateTime.now().toDateTime().toString());
        if(reference.child(id).setValue(order).isComplete()){
            flag = true;
        }
        return flag;
    }

}
