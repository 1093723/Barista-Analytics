package mini.com.baristaanalytics;

import android.animation.ArgbEvaluator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;

import java.util.ArrayList;
import java.util.List;

import Model.OkoaColdMenuAdapter;
import Model.OkoaColdMenuModel;

public class OkoaCategoryCold2 extends AppCompatActivity {

    ViewPager viewPager;
    OkoaColdMenuAdapter adapter;
    List<OkoaColdMenuModel> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okoa_category_cold2);

        models = new ArrayList<>();
        models.add(new OkoaColdMenuModel(R.drawable.brochure, "Brochure", "This is a brochure"));
        models.add(new OkoaColdMenuModel(R.drawable.sticker, "Sticker", "This is a sticker"));
        models.add(new OkoaColdMenuModel(R.drawable.poster, "Poster", "This is a poster"));
        models.add(new OkoaColdMenuModel(R.drawable.namecard, "Namecard", "This is a namecard"));

        adapter = new OkoaColdMenuAdapter(models, this);

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
}
