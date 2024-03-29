package com.rmw.photolibrary.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.rmw.photolibrary.model.ImageModel;
import com.rmw.photolibrary.repo.AuthRepository;
import com.rmw.photolibrary.repo.MainActivityRepository;
import java.util.ArrayList;

public class MainActivityViewModel extends AndroidViewModel {

    private final Application application;
    private AuthRepository authRepository;
    private MainActivityRepository mainActivityRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        authRepository = new AuthRepository(application);
        mainActivityRepository = new MainActivityRepository(application);
    }

    public void logOut() {
        authRepository.logOut();
    }

    public void deleteImage(String firebaseUserId, String refKey, ImageModel imageModel) {
        mainActivityRepository.deleteImage(firebaseUserId, refKey, imageModel);
    }

    public void saveImageToGallery(String imageRefUrl) {
        mainActivityRepository.saveImageToGallery(imageRefUrl);
    }

    public MutableLiveData<ArrayList<ImageModel>> loadImagesFromFirebase(String firebaseUserId) {
        return mainActivityRepository.getImageFromFirebase(firebaseUserId);
    }
}
