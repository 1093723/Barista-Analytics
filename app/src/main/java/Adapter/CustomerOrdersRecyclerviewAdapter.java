package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import Model.CoffeeOrder;
import mini.com.baristaanalytics.R;

public class CustomerOrdersRecyclerviewAdapter extends RecyclerView.Adapter<CustomerOrdersRecyclerviewAdapter.ViewHolder>{

    private List<CoffeeOrder> coffeeOrders;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomerOrdersRecyclerviewAdapter(List<CoffeeOrder> orders, Context context) {
        this.coffeeOrders = orders;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_orders_customers_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.description.setText(coffeeOrders.get(position).getOrder_Description());
        holder.date_ordered.setText(coffeeOrders.get(position).getOrder_date());
        holder.orderSummary.setText(coffeeOrders.get(position).getOrder_Total().toString());
        holder.confirmed.setText(coffeeOrders.get(position).getOrder_State());
    }

    @Override
    public int getItemCount() {
        return coffeeOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public Button confirmed;
        public TextView date_ordered, description,orderSummary;
        public ViewHolder(View itemView) {
            super(itemView);


            confirmed = itemView.findViewById(R.id.button_status);

            description = itemView.findViewById(R.id.description_hard);
            date_ordered = itemView.findViewById(R.id.date_ordered);
            orderSummary = itemView.findViewById(R.id.order_sum);        }
    }

}
