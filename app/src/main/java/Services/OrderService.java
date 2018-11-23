package Services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Model.CoffeeOrder;

public class OrderService {

    public OrderService() {
        }

    public boolean process_order(CoffeeOrder order,DatabaseReference reference){
        Boolean flag = true;
        String id = reference.push().getKey();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date_temp = new Date();
        String temp = dateFormat.format(date_temp);
        order.setOrder_Date(temp);
        Task task = reference.child(id).setValue(order);
        if(task.isSuccessful()){
            return true;
        }
        return flag;
    }

}
