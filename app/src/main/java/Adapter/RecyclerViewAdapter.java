package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mini.com.baristaanalytics.R;
import utilities.MessageItem;

import static android.content.ContentValues.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<MessageItem> message_items;
    private Context context;

    public RecyclerViewAdapter(List<MessageItem> message_items, Context context) {
        this.message_items = message_items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_massage, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        MessageItem message_item = message_items.get(position);
        holder.message.setText(message_item.getText());

    }

    @Override
    public int getItemCount() {
        return message_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;

        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
