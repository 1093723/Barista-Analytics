package mini.com.baristaanalytics.Order;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;

import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapter.CustomerOrdersRecyclerviewAdapter;
import Adapter.OkoaOrdersRecyclerviewAdapter;
import Adapter.SectionsPagerAdapter;
import Model.CoffeeOrder;
import mini.com.baristaanalytics.R;

import static utilities.MyApplication.CHANNEL_1_ID;

/**
 * This is the Customer-Side of viewing their current/previous orders
 */
public class CustomerOrders extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference coffeeList,coffee_Order;    // Speech to text
    private ArrayList<CoffeeOrder> coffeeOrderArrayList;
    private FirebaseDatabase database;
    private NotificationManagerCompat notificationManager;
    private Context ctx;
    private String TAG = "CUSTOMER ORDERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);
        //setupViewPager();
        ctx = this;
        database = FirebaseDatabase.getInstance();
        coffeeOrderArrayList = new ArrayList<>();
        notificationManager = NotificationManagerCompat.from(this);

        coffee_Order = database.getReference("OkoaCoffeeOrders");

        coffee_Order.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :
                        dataSnapshot.getChildren()) {
                    CoffeeOrder coffeeOrder = snap.getValue(CoffeeOrder.class);
                    if(!exists(coffeeOrder)){
                        // Update to order status
                        coffeeOrderArrayList.add(coffeeOrder);
                    }
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean exists(CoffeeOrder coffeeOrder) {
        if(coffeeOrderArrayList.size() >0){
            for (int i = 0; i < coffeeOrderArrayList.size(); i++) {
                CoffeeOrder temp = coffeeOrderArrayList.get(i);
                if(/*coffeeOrder.getOrder_Description().equals(temp.getOrder_Description())*/
                        coffeeOrder.getUUID().equals(temp.getUUID())
                        && coffeeOrder.getOrder_date().equals(temp.getOrder_date())
                        && (!coffeeOrder.getOrder_State().equals(temp.getOrder_State()))
                        ){
                    coffeeOrderArrayList.get(i).setOrder_State(coffeeOrder.getOrder_State());
                    sendNotification(coffeeOrder.getOrder_State());
                    return true;
                }
            }
        }
        return false;
    }

    private void sendNotification(String orderState) {
        String message = "";
        if(orderState.equals("Accepted")){
            message = "Your order is ready for collection.";
        }else message = "Your order has unfortunately been rejected";
        String title = "Barista Analytics Order Update";
        Intent intent = new Intent(this,CustomerOrders.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,intent,0);

        Notification notification =  new NotificationCompat.Builder(
                this,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_current_orders)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(1,notification);
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter  = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(new CustomerOrderFragment());
        //adapter.addFragments(new MessagesFragment());
        //adapter.addFragments(new OrderHistoryFragment());
        ViewPager viewPager = (ViewPager)findViewById(R.id.container);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_current_orders);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_converation);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_pervious_orders);
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCustomerConfirmed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerOrdersRecyclerviewAdapter(coffeeOrderArrayList,this);
        recyclerView.setAdapter(adapter);
    }
}
