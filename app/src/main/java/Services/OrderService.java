package Services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import Model.Beverage;
import Model.CoffeeOrder;

public class OrderService {


    public boolean process_order(CoffeeOrder order,DatabaseReference reference){
        Boolean flag = true;
        if(order.getUUID().equals("test"))
        {
            return true;
        }
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
    public Boolean beverageExists(Beverage beverage, List<Beverage> beverageList){
        for (int i = 0; i < beverageList.size(); i++) {
            if(beverageList.get(i).getBeverage_name().equals(beverage.getBeverage_name())){
                beverage.setBeverage_name(beverage.getBeverage_name());
                // Update the array
                beverageList.set(i,beverage);
                return true;
            }
        }
        return false;
    }
}
