package mini.com.baristaanalytics;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Test;

import Model.CoffeeOrder;
import Services.OrderService;

public class OrderServiceTests {
    @Test
    public void confirmOrder(){
        CoffeeOrder order = new CoffeeOrder();
        order.setUUID("test");
        OrderService orderService = new OrderService();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Assert.assertTrue(orderService.process_order(order,databaseReference));
    }
}
