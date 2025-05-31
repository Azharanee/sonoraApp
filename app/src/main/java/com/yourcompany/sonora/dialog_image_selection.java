package com.yourcompany.sonora;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class dialog_image_selection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set dialog style
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_dialog_image_selection);

        // Initialize your views and set click listeners here
        ImageView lofiImage = findViewById(R.id.lofi_image);
        ImageView podcastImage = findViewById(R.id.podcast_image);
        TextInputEditText collectionNameInput = findViewById(R.id.collection_name_input);

        // Set click listeners for images
        lofiImage.setOnClickListener(v -> {
            // Handle lofi image selection
        });

        podcastImage.setOnClickListener(v -> {
            // Handle podcast image selection
        });
    }
}