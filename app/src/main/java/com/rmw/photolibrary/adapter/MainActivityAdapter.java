package com.rmw.photolibrary.adapter;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.rmw.photolibrary.viewmodel.MainActivityViewModel;
import com.rmw.photolibrary.R;
import com.rmw.photolibrary.model.ImageModel;
import java.util.ArrayList;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {

    private final LayoutInflater inflater;
    private ArrayList<ImageModel> imageModelArrayList;
    private Context context;
    private MainActivityViewModel mainActivityViewModel;

    public MainActivityAdapter(Application application, Context ctx, ArrayList<ImageModel> imageModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
        this.context = ctx;
        mainActivityViewModel = new MainActivityViewModel(application);
    }

    @NonNull
    @Override
    public MainActivityAdapter.MainActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.main_activity_recycler_view, parent, false);
        return new MainActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.MainActivityViewHolder holder, int position) {
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

    public class MainActivityViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public MainActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.row_layout_image_view);

            itemView.setOnClickListener(this::showSaveImageAlertDialog);
            itemView.setOnLongClickListener(v -> {
                showDeleteImageAlertDialog(v);
                return true;
            });
        }

        private void showSaveImageAlertDialog(View v) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.save_image_alert_title)
                    .setMessage(R.string.save_image_alert_message)
                    .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                        // Save the image to phones gallery from Firebase storage
                        String imageRefUrl = imageModelArrayList.get(getAbsoluteAdapterPosition()).getImgRef();
                        mainActivityViewModel.saveImageToGallery(imageRefUrl);
                    })
                    .setNegativeButton(R.string.alert_no, null)
                    .show();
        }

        // Delete image alert dialog
        private void showDeleteImageAlertDialog(View v) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete_image_alert_title)
                    .setMessage(R.string.delete_image_alert_message)
                    .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                        // Delete image on positive button click using Firebase user unique id and image reference key
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String refKey = imageModelArrayList.get(getAbsoluteAdapterPosition()).getRefKey();
                        ImageModel imageModel = imageModelArrayList.get(getAbsoluteAdapterPosition());
                        mainActivityViewModel.deleteImage(uid, refKey, imageModel);
                    })
                    .setNegativeButton(R.string.alert_no, null)
                    .show();
        }
    }
}
