package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import Model.CoffeeOrder;
import mini.com.baristaanalytics.R;

public class OkoaOrdersRecyclerviewAdapter extends RecyclerView.Adapter<OkoaOrdersRecyclerviewAdapter.ViewHolder>{

    private List<CoffeeOrder> coffeeOrders;
    private LayoutInflater layoutInflater;
    private Context context;

    public OkoaOrdersRecyclerviewAdapter(List<CoffeeOrder> orders, Context context) {
        this.coffeeOrders = orders;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_orders_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(coffeeOrders.get(position).getOrder_CustomerUsername());
        holder.description.setText(coffeeOrders.get(position).getOrder_Description());
        holder.date_ordered.setText(coffeeOrders.get(position).getOrder_date());
        holder.orderSummary.setText(coffeeOrders.get(position).getOrder_Total().toString());

        if(coffeeOrders.get(position).getOrder_State().equals("Rejected") ||
                coffeeOrders.get(position).getOrder_State().equals("Confirmed")){
            holder.confirmed.setClickable(false);
            holder.rejected.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return coffeeOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public Button confirmed,rejected;
        public TextView date_ordered, description,name,orderSummary;
        public ViewHolder(View itemView) {
            super(itemView);


            confirmed = itemView.findViewById(R.id.confirm_button);
            rejected = itemView.findViewById(R.id.reject_button);

            name = itemView.findViewById(R.id.name_surname_user);
            description = itemView.findViewById(R.id.description_hard);
            date_ordered = itemView.findViewById(R.id.date_ordered);
            orderSummary = itemView.findViewById(R.id.order_sum);        }
    }

    /*
    @Override
    public int getCount() {
        return coffeeOrders.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.current_orders_item, container, false);


    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
        }
        return bm;
    }*/
}