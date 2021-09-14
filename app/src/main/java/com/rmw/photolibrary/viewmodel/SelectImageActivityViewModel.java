package com.rmw.photolibrary.viewmodel;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.rmw.photolibrary.repo.AuthRepository;
import com.rmw.photolibrary.repo.SelectImageActivityRepository;

public class SelectImageActivityViewModel extends AndroidViewModel {

    private Application application;
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

    public void uploadImage(Uri filePath, String firebaseUserId) {
        selectImageActivityRepository.uploadImageToFirebase(filePath, firebaseUserId);
    }
}
