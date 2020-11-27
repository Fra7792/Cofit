package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.model.ModelFile;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapterNovita extends RecyclerView.Adapter<CustomAdapterNovita.ImageViewHolder> {

    private Context mContext;
    private List<ModelFile> mUploads;

    public CustomAdapterNovita(Context context, List<ModelFile> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.lv_item_novita, parent, false);
        return  new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        ModelFile uploadCurrent = mUploads.get(position);
        Picasso.get()
                .load(uploadCurrent.getFileUrl())
                .fit()
                .centerInside()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public PhotoView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageview_novita);
        }
    }
}
