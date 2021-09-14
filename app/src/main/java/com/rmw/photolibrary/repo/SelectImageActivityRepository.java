package com.rmw.photolibrary.repo;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rmw.photolibrary.R;
import java.util.UUID;

public class SelectImageActivityRepository {

    private final Application application;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    public SelectImageActivityRepository(Application application) {
        this.application = application;
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
                    .addOnSuccessListener(taskSnapshot -> {
                        /* Img has been uploaded to firebase storage successfully
                          now get the img download url and save it to the real time database */
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save img download url in real time database
                            // img url is saved with the users Firebase unique id so each user only has access to their own data
                            Log.d("TAG", "Download URL = " + uri.toString());
                            Toast.makeText(application, R.string.toast_uploaded, Toast.LENGTH_SHORT).show();
                            databaseReference.child(firebaseUserId).push().setValue(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show(); // TODO - was context
                    });
        }
    }
}
