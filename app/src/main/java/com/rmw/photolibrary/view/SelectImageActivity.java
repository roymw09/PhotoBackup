package com.rmw.photolibrary.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rmw.photolibrary.R;
import com.rmw.photolibrary.adapter.SelectImageActivityAdapter;
import com.rmw.photolibrary.model.ImageModel;
import com.rmw.photolibrary.viewmodel.SelectImageActivityViewModel;
import java.util.ArrayList;

public class SelectImageActivity extends AppCompatActivity {

    private SelectImageActivityViewModel selectImageActivityViewModel;
    private String firebaseUserId;
    private RecyclerView recyclerView;
    private SelectImageActivityAdapter adapter;
    private ArrayList<ImageModel> imageModelRecyclerArrayList;
    private ArrayList<Uri> filePathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectImageActivityViewModel = new ViewModelProvider(this).get(SelectImageActivityViewModel.class);
        recyclerView = findViewById(R.id.activity_select_image_recyclerview);
        imageModelRecyclerArrayList = new ArrayList<>();


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

        initRecycler();
    }

    private void initRecycler() {
        adapter = new SelectImageActivityAdapter(getApplication(), this, imageModelRecyclerArrayList);
        // Display RecyclerView as a 2 row grid
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        // user selects multiple images at once
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            int currentItem = 0;
                            while (currentItem < count) {
                                // Get image uri
                                Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();

                                // Store the image uri an ImageModel
                                ImageModel model = new ImageModel();
                                model.setImgRef(imageUri.toString());
                                model.setRefKey("");

                                // add the model to the recyclerArray so our selected images will appear
                                imageModelRecyclerArrayList.add(model);

                                // add imageUri to the filePathList so the user can upload the select images
                                filePathList.add(imageUri);
                                currentItem++;
                            }
                            // user selects a single image
                        } else if (data.getData() != null) {
                            // Store the image uri an ImageModel
                            ImageModel model = new ImageModel();
                            model.setImgRef(data.getData().toString()); // get img uri
                            model.setRefKey("");
                            imageModelRecyclerArrayList.add(model);

                            filePathList.add(data.getData()); // add the image url

                        }

                    }
                    // display selected images
                    displaySelectedImage();
                }
            });

    private void displaySelectedImage() {
        if (imageModelRecyclerArrayList != null) {
            adapter.updateList(imageModelRecyclerArrayList);
        }
    }

    private void clearSelectedImage(View v) {
        filePathList.clear(); // clear list of selected image file paths
        imageModelRecyclerArrayList.clear(); // clear list of image models
        adapter.updateList(imageModelRecyclerArrayList); // refresh the RecyclerView
    }

    private void selectImageIntent(View v) {
        // Launch an intent to select an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
        selectImageActivityViewModel.uploadImage(filePathList, firebaseUserId);
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
