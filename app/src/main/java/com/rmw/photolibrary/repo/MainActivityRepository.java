package com.rmw.photolibrary.repo;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rmw.photolibrary.R;
import com.rmw.photolibrary.model.ImageModel;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivityRepository {

    private final static String ALBUM_NAME = "PhotoLib";
    private final Application application;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private MutableLiveData<ArrayList<ImageModel>> allImages;
    private ArrayList<ImageModel> imageList;

    public MainActivityRepository(Application application) {
        this.application = application;
        allImages = new MutableLiveData<>();
        imageList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    public void deleteImage(String firebaseUserId, String refKey, ImageModel imageModel) {
        // Delete image from storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageModel.getImgRef());
        imageRef.delete().addOnSuccessListener(aVoid -> {
            // If storage deletion is successful, delete the image reference from the database
            DatabaseReference databaseReference = database.getReference()
                    .child(firebaseUserId)
                    .child(refKey);
            databaseReference.removeValue();
            Toast.makeText(application, R.string.toast_deleted_image, Toast.LENGTH_SHORT).show();
        });

        DatabaseReference databaseReference = database.getReference()
                .child(firebaseUserId)
                .child(refKey);
        databaseReference.removeValue();
        Toast.makeText(application, R.string.toast_deleted_image, Toast.LENGTH_SHORT).show();
    }

    // Save image to users' gallery
    public void saveImageToGallery(String imageRefUrl) {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String imageName = time + ".PNG";
        // Downloads image from Firebase storage using the imageRefUrl
        try {
            DownloadManager downloadManager = (DownloadManager) application.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri imageRefUri = Uri.parse(imageRefUrl);
            DownloadManager.Request request = new DownloadManager.Request(imageRefUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(imageName)
                    .setMimeType("image/png")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                            File.separator + ALBUM_NAME + File.separator + imageName);

            downloadManager.enqueue(request);
            Toast.makeText(application, R.string.toast_image_saved_to_gallery, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Retrieves all images from Firebase storage
    public MutableLiveData<ArrayList<ImageModel>> getImageFromFirebase(String firebaseUserId) {
        // Get the img reference from the database
        databaseReference.child(firebaseUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                // Clear the imageList to prevent duplicate data
                imageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Get the img url from the database
                    String url = dataSnapshot.getValue(String.class);
                    ImageModel modelRecycler = new ImageModel();
                    modelRecycler.setImgRef(url);
                    // refKey is used to delete image references from firebase database
                    modelRecycler.setRefKey(dataSnapshot.getKey());
                    imageList.add(modelRecycler);
                }
                allImages.postValue(imageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Database Error: ", error.toString());
            }
        });

        return allImages;
    }
}
