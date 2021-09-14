package com.rmw.photolibrary.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.rmw.photolibrary.R;
import com.rmw.photolibrary.adapter.MainActivityAdapter;
import com.rmw.photolibrary.model.ImageModel;
import com.rmw.photolibrary.viewmodel.MainActivityViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerView;
    private ArrayList<ImageModel> imageModelRecyclerArrayList;
    private MainActivityAdapter adapter;
    private String firebaseUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        recyclerView = findViewById(R.id.activity_main_recyclerView);
        imageModelRecyclerArrayList = new ArrayList<>();

        /*verify that the content passed from our last activity is not null, then
          get the signed in users firebase user id, initialize the RecyclerView, and display the users data */
        if (getIntent().getExtras() != null) {
            firebaseUserId = (String) getIntent().getExtras().get("uid");
            initRecycler();
            displayUserImages(firebaseUserId);
        }
    }

    private void initRecycler() {
        adapter = new MainActivityAdapter(getApplication(), this, imageModelRecyclerArrayList);
        // Display RecyclerView as a 2 row grid
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    private void goToUpload() {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putExtra("uid", firebaseUserId);
        startActivity(intent);
    }

    private void logout() {
        mainActivityViewModel.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayUserImages(String firebaseUserId) {
        mainActivityViewModel.loadImagesFromFirebase(firebaseUserId).observe(this, imageModels -> {
            if (imageModels != null) {
                // get the imageModels containing the storage ref url
                imageModelRecyclerArrayList = imageModels;
                // once the adapter list is updated our data will be displayed in the RecyclerView
                adapter.updateList(imageModelRecyclerArrayList);
            }
        });
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
            case R.id.action_selectImages:
                goToUpload();
                return true;

            case R.id.action_logOut:
                logout();
                return true;
        }
        return false;
    }
}
