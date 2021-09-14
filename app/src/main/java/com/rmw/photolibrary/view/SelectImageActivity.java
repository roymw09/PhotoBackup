package com.rmw.photolibrary.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.rmw.photolibrary.R;
import com.rmw.photolibrary.view.LoginActivity;
import com.rmw.photolibrary.view.MainActivity;
import com.rmw.photolibrary.viewmodel.SelectImageActivityViewModel;

import java.io.IOException;

public class SelectImageActivity extends AppCompatActivity {

    private SelectImageActivityViewModel selectImageActivityViewModel;
    private ImageView selectedImage;
    private Uri filePath; // img filepath
    private String firebaseUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectImageActivityViewModel = new ViewModelProvider(this).get(SelectImageActivityViewModel.class);

        selectedImage = findViewById(R.id.activity_selectImage_image_view);

        Button selectButton = findViewById(R.id.activity_selectImage_selectButton);
        selectButton.setOnClickListener(this::selectImageIntent);

        Button uploadButton = findViewById(R.id.activity_selectImage_uploadButton);
        uploadButton.setOnClickListener(this::upload);

        Button clearButton = findViewById(R.id.activity_selectImage_clearButton);
        clearButton.setOnClickListener(this::clearSelectedImage);

        // Get firebaseUserId from the previous activity
        if (getIntent().getExtras() != null) {
            firebaseUserId = (String) getIntent().getExtras().get("uid");
        }
    }

    // Retrieves filePath and ImageBitmap from select image intent
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        filePath = data.getData();
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), filePath);
                            Bitmap image = ImageDecoder.decodeBitmap(source);
                            selectedImage.setImageBitmap(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void clearSelectedImage(View v) {
        filePath = null;
        selectedImage.setImageBitmap(null);
    }

    private void selectImageIntent(View v) {
        // Launch an intent to select an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    }

    private void logout() {
        selectImageActivityViewModel.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("uid", firebaseUserId);
        startActivity(intent);
    }

    // Upload img to Firebase storage and save img reference in real time database
    private void upload(View v) {
        selectImageActivityViewModel.uploadImage(filePath, firebaseUserId);
        clearSelectedImage(v);
    }

    // Inflate menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_images:
                goToMainActivity();
                return true;

            case R.id.action_logOut:
                logout();
                return true;
        }
        return false;
    }
}
