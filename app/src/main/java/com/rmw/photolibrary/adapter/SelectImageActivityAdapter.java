package com.rmw.photolibrary.adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.rmw.photolibrary.R;
import com.rmw.photolibrary.model.ImageModel;
import java.util.ArrayList;

public class SelectImageActivityAdapter extends RecyclerView.Adapter<SelectImageActivityAdapter.SelectActivityViewHolder> {

    private final LayoutInflater inflater;
    private ArrayList<ImageModel> imageModelArrayList;
    private Context context;

    public SelectImageActivityAdapter(Application application, Context ctx, ArrayList<ImageModel> imageModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
        this.context = ctx;
    }

    @NonNull
    @Override
    public SelectImageActivityAdapter.SelectActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.select_activity_recycler_view, parent, false);
        return new SelectActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectImageActivityAdapter.SelectActivityViewHolder holder, int position) {
        String imageUrl = imageModelArrayList.get(position).getImgRef(); // get image reference url
        Glide.with(context).load(imageUrl).into(holder.image); // load image to textview with Glide
    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public void updateList(ArrayList<ImageModel> updatedList) {
        imageModelArrayList = updatedList;
        notifyDataSetChanged();
    }

    public class SelectActivityViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public SelectActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.select_activity_row_layout_image_view);
        }
    }
}