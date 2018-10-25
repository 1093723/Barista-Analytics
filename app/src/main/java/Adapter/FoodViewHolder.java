package Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import Interface.ItemClickListener;
import mini.com.baristaanalytics.R;


public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView food_txt_viewName;
    public ImageView food_image;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_txt_viewName = itemView.findViewById(R.id.food_name);
        food_image = itemView.findViewById(R.id.food_item);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
