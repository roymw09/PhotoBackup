package com.rmw.photolibrary.repo;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rmw.photolibrary.R;
import com.rmw.photolibrary.model.ImageModel;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.UUID;

public class SelectImageActivityRepository {

    private final Application application;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private MutableLiveData<ArrayList<ImageModel>> allImages;
    private ArrayList<ImageModel> imageList;

    public SelectImageActivityRepository(Application application) {
        this.application = application;
        allImages = new MutableLiveData<>();
        imageList = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    // Upload img to Firebase storage and save img reference in real time database
    public void uploadImageToFirebase(Uri filePath, String firebaseUserId) {
        if (filePath != null) {
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            // Upload the selected image to Firebase storage
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            /* Img has been uploaded to firebase storage successfully
                              now get the img download url and save it to the real time database */
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Save img download url in real time database
                                    // img url is saved with the users Firebase unique id so each user only has access to their own data
                                    Log.d("TAG", "Download URL = " + uri.toString());
                                    Toast.makeText(application, R.string.toast_uploaded, Toast.LENGTH_SHORT).show();
                                    databaseReference.child(firebaseUserId).push().setValue(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show(); // TODO - was context
                        }
                    });
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
