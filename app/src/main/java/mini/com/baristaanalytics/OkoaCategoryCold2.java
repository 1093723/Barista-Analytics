package mini.com.baristaanalytics;

import android.animation.ArgbEvaluator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Model.Beverage;
import Model.OkoaColdMenuAdapter;
import Model.OkoaColdMenuModel;

public class OkoaCategoryCold2 extends AppCompatActivity {
    private final String TAG = "OKOA_COLD_CATEGORY";
    ViewPager viewPager;
    OkoaColdMenuAdapter adapter;
    List<Beverage> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    FirebaseDatabase database;
    DatabaseReference coffeeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okoa_category_cold2);
        models = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        coffeeList = database.getReference("CoffeeMenuOkoa");
        coffeeList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :
                        dataSnapshot.getChildren()) {
                    Beverage beverage = snap.getValue(Beverage.class);
                    models.add(beverage);
                    //Log.d(TAG, beverage.getBeverage_name());
                }
                /*models.add(new OkoaColdMenuModel(R.drawable.brochure, "Brochure", "This is a brochure"));
                models.add(new OkoaColdMenuModel(R.drawable.sticker, "Sticker", "This is a sticker"));
                models.add(new OkoaColdMenuModel(R.drawable.poster, "Poster", "This is a poster"));
                models.add(new OkoaColdMenuModel(R.drawable.namecard, "Namecard", "This is a namecard"));
                */
                adapter = new OkoaColdMenuAdapter(models, OkoaCategoryCold2.this);

                viewPager = findViewById(R.id.viewPager);
                viewPager.setAdapter(adapter);
                viewPager.setPadding(130,0,130,0);
                Integer[] colors_temp = {
                        getResources().getColor(R.color.color5),
                        getResources().getColor(R.color.color2),
                        getResources().getColor(R.color.color3),
                        getResources().getColor(R.color.color4)
                };

                colors = colors_temp;
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        if(position < (adapter.getCount() - 1) && position < (colors.length - 1)){
                            viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                        }
                        else {
                            viewPager.setBackgroundColor(colors[colors.length - 1]);
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }
}
