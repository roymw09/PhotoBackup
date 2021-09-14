package com.rmw.photolibrary.adapter;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
    private String refKey;

    public MainActivityAdapter(Application application, Context ctx, ArrayList<ImageModel> imageModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
        this.context = ctx;
        mainActivityViewModel = new MainActivityViewModel(application);
    }

    @NonNull
    @Override
    public MainActivityAdapter.MainActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_layout, parent, false);
        return new MainActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.MainActivityViewHolder holder, int position) {
        String imageUrl = imageModelArrayList.get(position).getImgRef(); // get image reference url
        refKey = imageModelArrayList.get(position).getRefKey();
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
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteImageAlertDialog(v);
                    return true;
                }
            });
        }

        private void showSaveImageAlertDialog(View v) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.save_image_alert_title)
                    .setMessage(R.string.save_image_alert_message)
                    .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Get the bitmap from the ImageView
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
                            Bitmap imageBitmap = bitmapDrawable.getBitmap();
                            // Save the image to phones gallery
                            mainActivityViewModel.saveImageToGallery(imageBitmap);
                        }
                    })
                    .setNegativeButton(R.string.alert_no, null)
                    .show();
        }

        private void showDeleteImageAlertDialog(View v) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete_image_alert_title)
                    .setMessage(R.string.delete_image_alert_message)
                    .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainActivityViewModel.deleteImage(FirebaseAuth.getInstance().getCurrentUser().getUid(), refKey);
                        }
                    })
                    .setNegativeButton(R.string.alert_no, null)
                    .show();
        }
    }
}
