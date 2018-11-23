package mini.com.baristaanalytics.Order;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;

import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import Adapter.CustomerOrdersRecyclerviewAdapter;
import Adapter.SectionsPagerAdapter;
import Model.CoffeeOrder;
import mini.com.baristaanalytics.Account_Management.LoginActivity;
import mini.com.baristaanalytics.R;
import utilities.ConnectivityReceiver;

import static utilities.MyApplication.CHANNEL_1_ID;

/**
 * This is the Customer-Side of viewing their current/previous orders
 */
public class CustomerOrders extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference doubleshot,coffee_Order;    // Speech to text
    private ArrayList<CoffeeOrder> coffeeOrderArrayList;
    private FirebaseDatabase database;
    private NotificationManagerCompat notificationManager;
    private Context ctx;
    private String TAG = "CUSTOMER ORDERS";
    private FirebaseAuth firebaseAuth;
    private String firebaseUserName;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onPause() {
        super.onPause();
        animationDrawable.stop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);
        //setupViewPager();
        ctx = this;
        database = FirebaseDatabase.getInstance();
        coffeeOrderArrayList = new ArrayList<>();
        notificationManager = NotificationManagerCompat.from(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUserName = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
        coffee_Order = database.getReference("OkoaCoffeeOrders");
        doubleshot = database.getReference("DoubleshotCoffeeOrders");
        progressBar = findViewById(R.id.cust_orders_progress);        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCustomerConfirmed);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCustomerConfirmed);
        relativeLayout = findViewById(R.id.bruceCustomerOrder);
        animationDrawable = (AnimationDrawable)  relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
        new WaitingTime().execute(3);

        doubleshot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :
                        dataSnapshot.getChildren()) {
                    CoffeeOrder coffeeOrder = snap.getValue(CoffeeOrder.class);
                    if(!exists(coffeeOrder)){
                        // Update to order status
                        if(coffeeOrder.getUUID().equals(firebaseAuth.getUid())){
                            coffeeOrderArrayList.add(coffeeOrder);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        coffee_Order.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :
                        dataSnapshot.getChildren()) {
                    CoffeeOrder coffeeOrder = snap.getValue(CoffeeOrder.class);
                    if(!exists(coffeeOrder)){
                        // Update to order status
                        if(coffeeOrder.getUUID().equals(firebaseAuth.getUid())){
                            coffeeOrderArrayList.add(coffeeOrder);
                        }
                    }
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        updateUI(firebaseAuth);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(animationDrawable != null){
            animationDrawable.start();
        }
        updateUI(firebaseAuth);
    }

    private void updateUI(FirebaseAuth firebaseAuth){
        if(firebaseAuth.getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean exists(CoffeeOrder coffeeOrder) {
        Boolean exists = false;
        if(coffeeOrderArrayList.size() >0){
            for (int i = 0; i < coffeeOrderArrayList.size(); i++) {
                CoffeeOrder temp = coffeeOrderArrayList.get(i);
                if(coffeeOrder.getUUID().equals(temp.getUUID())
                        && coffeeOrder.getOrder_Date().equals(temp.getOrder_Date())
                        ){
                    coffeeOrderArrayList.get(i).setOrder_Rating(coffeeOrder.getOrder_Rating());
                    exists = true;
                    if(!coffeeOrder.getOrder_State().equals(temp.getOrder_State())){
                        sendNotification(coffeeOrder.getOrder_State());
                        coffeeOrderArrayList.get(i).setOrder_State(coffeeOrder.getOrder_State());
                    }
                }
            }
        }
        return exists;
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerOrdersRecyclerviewAdapter(coffeeOrderArrayList,this);
        recyclerView.setAdapter(adapter);
    }

    public void sign_Out(View view) {
        firebaseAuth.signOut();
        updateUI(firebaseAuth);
    }

    public void promptSpeechInput(View view) {
        boolean isConnected = ConnectivityReceiver.isConnected();

        if(isConnected){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt));
            animationDrawable.stop();
            try {
                //initRecyclerView();
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.speech_not_supported),
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            showToast(isConnected);
        }

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

    class WaitingTime extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            for (int count = 1; count <= params[0]; count++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.animate().alpha(1.0f).setDuration(12000);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
    class SignOutWait extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            for (int count = 1; count <= params[0]; count++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
}
