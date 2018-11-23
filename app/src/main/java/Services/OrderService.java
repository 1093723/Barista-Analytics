package Services;

import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Model.CoffeeOrder;

public class OrderService {

    public OrderService() {
        }

    public boolean process_order(CoffeeOrder order,DatabaseReference reference){
        Boolean flag = false;
        String id = reference.push().getKey();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date_temp = new Date();
        String temp = dateFormat.format(date_temp);
        order.setOrder_Date(temp);
        if(reference.child(id).setValue(order).isSuccessful()){
            flag = true;
        }
        return flag;
    }

}
