package com.rmw.photolibrary.viewmodel;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.rmw.photolibrary.repo.AuthRepository;
import com.rmw.photolibrary.repo.SelectImageActivityRepository;
import java.util.ArrayList;

public class SelectImageActivityViewModel extends AndroidViewModel {

    private final Application application;
    private AuthRepository authRepository;
    private SelectImageActivityRepository selectImageActivityRepository;

    public SelectImageActivityViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        authRepository = new AuthRepository(application);
        selectImageActivityRepository = new SelectImageActivityRepository(application);
    }

    public void logOut() {
        authRepository.logOut();
    }

    public void uploadImage(ArrayList<Uri> filePath, String firebaseUserId) {
        selectImageActivityRepository.uploadImageToFirebase(filePath, firebaseUserId);
    }
}
