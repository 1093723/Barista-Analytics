package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.util.List;

import Model.Barista;
import Model.CoffeeOrder;
import mini.com.baristaanalytics.Account_Management.LoginActivity;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.description.setText(coffeeOrders.get(position).getOrder_Description());
        holder.date_ordered.setText(coffeeOrders.get(position).getOrder_date());
        holder.orderSummary.setText("R" + coffeeOrders.get(position).getOrder_Total().toString());
        holder.confirmed.setText(coffeeOrders.get(position).getOrder_State());
        /*if(!flag){
            flag = true;
        }*/holder.ratingBar.setRating(coffeeOrders.get(position).getOrder_rating());
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                //holder.ratingBar.setRating(coffeeOrders.get(position).getOrder_rating());
                updateOrder(v,coffeeOrders.get(position),holder);
                updateRating(v,holder);
            }
        });
    }

    private void updateOrder(final float rating, final CoffeeOrder coffeeOrder, final ViewHolder holder) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("OkoaCoffeeOrders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    CoffeeOrder tempOrder = snap.getValue(CoffeeOrder.class);
                    if(tempOrder.getUUID().equals(coffeeOrder.getUUID())
                            && tempOrder.getOrder_date().equals(coffeeOrder.getOrder_date())){
                        tempOrder.setOrder_rating(rating);
                        String key = snap.getKey();
                        databaseReference.child(key).setValue(tempOrder);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateRating(final Float rating, final CustomerOrdersRecyclerviewAdapter.ViewHolder holder){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("COFFEEPLACES");
        final Float lastRating  = rating;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Barista barista = snap.getValue(Barista.class);
                    if(barista.getName().contains("Okoa")
                            ){
                        Float avg = (barista.getRating() + lastRating)/2;
                        barista.setRating(avg);
                        String key = snap.getKey();
                        //holder.ratingBar.setClickable(false);
                        //holder.ratingBar.setActivated(false);
                        //holder.ratingBar.setEnabled(false);
                        databaseReference.child(key).setValue(barista);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return coffeeOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public Button sign_out;
        public Button confirmed;
        public RatingBar ratingBar;
        public TextView date_ordered, description,orderSummary;
        public ViewHolder(View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating_coffee_order);
            confirmed = itemView.findViewById(R.id.button_status);
            sign_out = itemView.findViewById(R.id.btnSignOut);
            description = itemView.findViewById(R.id.description_hard);
            date_ordered = itemView.findViewById(R.id.date_ordered);
            orderSummary = itemView.findViewById(R.id.order_sum);        }
    }

}
