package mini.com.baristaanalytics.Order;

import android.animation.ArgbEvaluator;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapter.OkoaOrdersRecyclerviewAdapter;
import Adapter.SectionsPagerAdapter;
import Model.CoffeeOrder;
import mini.com.baristaanalytics.Account_Management.LoginActivity;
import mini.com.baristaanalytics.R;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;
import utilities.MyApplication;

import static utilities.MyApplication.CHANNEL_2_ID;

/**
 *This is the admin-side of viewing and managing customer orders
 */
public class OrderConfirmedActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    private String TAG = "ORDER CONFIRMED";
    private Context ctx;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private CoffeeOrder coffeeOrder;
    private DatabaseReference coffeeList,coffee_Order;    // Speech to text
    private ArrayList<CoffeeOrder> coffeeOrderArrayList;
    private NotificationManagerCompat notificationManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FirebaseAuth firebaseAuth;

    private Integer notificationCount;
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showToast(isConnected);
    }

    @Override
    protected void onPause(){
        super.onPause();
        notificationCount =0;
        //unregisterReceiver(connectivityReceiver);
    }
    @Override
    protected void onStart(){
        super.onStart();
        updateUI(mAuth);
    }

    private void updateUI(FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        notificationCount=0;
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateUI(mAuth);
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);
        Log.d(TAG,"onCreate:Activity Started");
        notificationCount = 1;
        firebaseAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        notificationManager = NotificationManagerCompat.from(this);
        ctx = OrderConfirmedActivity.this;
        coffeeOrderArrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
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
                        notifyCustomers(coffeeOrder);
                    }
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //setupViewPager();
    }
    private void notifyCustomers(CoffeeOrder coffeeOrder){
            if(coffeeOrder.getOrder_State().equals("Ordered")){
                sendNotification();
                notificationCount+=1;
        }
    }
    private boolean exists(CoffeeOrder coffeeOrder) {
        Boolean flag = false;
        if(coffeeOrderArrayList.size() >0){

            for (int i = 0; i < coffeeOrderArrayList.size(); i++) {
                CoffeeOrder temp = coffeeOrderArrayList.get(i);
                if(
                        coffeeOrder.getUUID().equals(temp.getUUID())
                                && coffeeOrder.getOrder_date().equals(temp.getOrder_date())
                        ){
                    // This is a new coffee order(not in arraylist)
                    // Need to check if the status is 'Ordered'
                    return true;
                }

            }
        }
        return false;
    }

    private void sendNotification() {
        String message = "New order received. Click to review and process this order";

        String title = "Barista Analytics Order Update";
        String notficationGroup = "Customer Orders";
        Intent intent = new Intent(this,CustomerOrders.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,intent,0);

        Notification notification =  new NotificationCompat.Builder(
                this,CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_current_orders)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText(notificationCount + " New Orders")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroup(notficationGroup)
                .setGroupSummary(true)
                .build();

        notificationManager.notify(1,notification);

    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewConfirmed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OkoaOrdersRecyclerviewAdapter(coffeeOrderArrayList,this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Initialize amazon polly
     */
    public void signOut(View view) {
        firebaseAuth.signOut();
        updateUI(firebaseAuth);
    }
    private void showToast(boolean isConnected) {
        String message = "Checking";
        if (isConnected) {
            message = "Good! Connected to Internet";
            //this.btn.setClickable(true);

        } else {
            message = "Sorry! Please connect to the internet to proceed";
            //this.btn.setClickable(false);
        }
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

}
