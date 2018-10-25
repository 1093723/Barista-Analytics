package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import utilities.Upload;
import mini.com.baristaanalytics.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context ctx;
    private List<Upload> mUploads;

    public ImageAdapter(Context ctx, List<Upload> mUploads) {
        this.ctx = ctx;
        this.mUploads = mUploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.image_item, parent,false);
        return  new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload mCurrent = mUploads.get(position);
        holder.textViewName.setText(mCurrent.getTxtName());
        Picasso.with(ctx)
                .load(mCurrent.getImgUrl())
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewName;
        public ImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.txtViewName);
            imageView = itemView.findViewById(R.id.imgViewPlace);

        }
    }
}
