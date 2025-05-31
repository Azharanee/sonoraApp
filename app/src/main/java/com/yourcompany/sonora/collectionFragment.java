package com.yourcompany.sonora;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class collectionFragment extends Fragment {
    private CollectionDatabaseHelper dbHelper;
    private List<Collection> collectionList = new ArrayList<>();
    private CollectionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        dbHelper = new CollectionDatabaseHelper(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.collections_recycler_view);

        // Set GridLayoutManager with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CollectionAdapter(getContext(), collectionList, this::onCollectionClick);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showAddCollectionDialog());

        loadCollections();
        return view;
    }

    private void loadCollections() {
        collectionList.clear();
        Cursor cursor = dbHelper.getAllCollections();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndexOrThrow("id");
                int nameColumnIndex = cursor.getColumnIndexOrThrow("name");
                int imageTypeColumnIndex = cursor.getColumnIndexOrThrow("image_type");

                int id = cursor.getInt(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String imageType = cursor.getString(imageTypeColumnIndex);
                collectionList.add(new Collection(id, name, imageType));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddCollectionDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_dialog_image_selection, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        ImageView lofiImage = dialogView.findViewById(R.id.lofi_image);
        ImageView podcastImage = dialogView.findViewById(R.id.podcast_image);
        TextInputEditText nameInput = dialogView.findViewById(R.id.collection_name_input);

        final String[] selectedImageType = {null};
        lofiImage.setOnClickListener(v -> selectedImageType[0] = "lofi");
        podcastImage.setOnClickListener(v -> selectedImageType[0] = "podcast");

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            if (!name.isEmpty() && selectedImageType[0] != null) {
                long newCollectionId = dbHelper.addCollection(name, selectedImageType[0]);
                if (newCollectionId != -1) {
                    Toast.makeText(getContext(), "Collection '" + name + "' saved!", Toast.LENGTH_SHORT).show();
                    showMusicSelectionDialog((int) newCollectionId);
                    loadCollections();
                } else {
                    Toast.makeText(getContext(), "Failed to save collection.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter a name and select an image.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void onCollectionClick(Collection collection) {
        Toast.makeText(getContext(), "Clicked on collection: " + collection.name, Toast.LENGTH_SHORT).show();
    }

    private void showMusicSelectionDialog(int collectionId) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item_collection, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        RecyclerView musicRecyclerView = dialogView.findViewById(R.id.collection_field);
        musicRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<MusicFiles> allMusicFiles = new ArrayList<>();
        if (MainActivity.musicFiles != null && !MainActivity.musicFiles.isEmpty()) {
            allMusicFiles.addAll(MainActivity.musicFiles);
        } else {
            Toast.makeText(getContext(), "Music files not loaded yet. Please grant permissions in Home tab.", Toast.LENGTH_LONG).show();
            return;
        }

        MusicAdapter musicAdapter = new MusicAdapter(getContext(), allMusicFiles, musicFile -> {
            long result = dbHelper.addMusicToCollection(collectionId, musicFile.getPath());
            if (result != -1) {
                Toast.makeText(getContext(), "Added '" + musicFile.getTitle() + "' to collection.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add music to collection.", Toast.LENGTH_SHORT).show();
            }
        });

        musicRecyclerView.setAdapter(musicAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}