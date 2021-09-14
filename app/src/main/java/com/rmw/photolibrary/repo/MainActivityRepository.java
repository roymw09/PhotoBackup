package com.rmw.photolibrary.repo;

import android.app.Application;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmw.photolibrary.R;
import com.rmw.photolibrary.model.ImageModel;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivityRepository {

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

    public void deleteImage(String firebaseUserId, String refKey) {
        DatabaseReference databaseReference = database.getReference()
                .child(firebaseUserId)
                .child(refKey);
        databaseReference.removeValue();
        Toast.makeText(application, R.string.toast_deleted_image, Toast.LENGTH_SHORT).show();
    }

    // Save image to users' gallery
    public void saveImageToGallery(Bitmap imageBitmap) {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String imageName = time + ".PNG";
        try {
            MediaStore.Images.Media
                    .insertImage(application.getContentResolver(), imageBitmap, imageName, "From PhotoLib");
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
