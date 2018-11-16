package utilities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    public static final String CHANNEL_1_ID = "CUSTOMER_ORDER_TRACKING";
    public static final String CHANNEL_2_ID = "ADMIN_ORDER_TRACKING";
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        createNotificationChannels();
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    private void createNotificationChannels() {
        // Check if we're in Android Oreo

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Supports notification channels
            NotificationChannel CUST_ORDER_TRACK = new
                    NotificationChannel(CHANNEL_1_ID,
                    "Track Your Order",
                    NotificationManager.IMPORTANCE_HIGH);
            CUST_ORDER_TRACK.setDescription("This keeps track of order statuses updates " +
                    "being made by the admin on the users' order");
            NotificationChannel ADMIN_ORDER_TRACK = new
                    NotificationChannel(CHANNEL_2_ID,
                    "Track Your Order",
                    NotificationManager.IMPORTANCE_LOW);
            ADMIN_ORDER_TRACK.setDescription("This keeps track of orders being made by " +
                    "a user to the admin");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(CUST_ORDER_TRACK);
            manager.createNotificationChannel(ADMIN_ORDER_TRACK);
        }

    }
}