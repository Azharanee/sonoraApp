package com.yourcompany.sonora;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
// Import your MainActivity to access the static music list
import com.yourcompany.sonora.MainActivity;

public class collectionFragment extends Fragment {

    private CollectionDatabaseHelper dbHelper;
    private List<Collection> collectionList = new ArrayList<>();
    private CollectionAdapter adapter;

    // TODO: These parameters are from the boilerplate.
    // If you don't need them, you can remove them and the newInstance factory method.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public collectionFragment() {
        // Required empty public constructor
    }

    // TODO: If you removed the ARG_PARAMs, update or remove this factory method.
    public static collectionFragment newInstance(String param1, String param2) {
        collectionFragment fragment = new collectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        dbHelper = new CollectionDatabaseHelper(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.collections_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // When a collection is clicked, call onCollectionClick
        adapter = new CollectionAdapter(getContext(), collectionList, this::onCollectionClick);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showAddCollectionDialog());

        // Load existing collections when the fragment is created
        loadCollections();

        return view;
    }

    // Method to load collections from the database and update the RecyclerView
    private void loadCollections() {
        collectionList.clear();
        Cursor cursor = dbHelper.getAllCollections(); // Get data from the database
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Get column indices safely
                int idColumnIndex = cursor.getColumnIndexOrThrow("id");
                int nameColumnIndex = cursor.getColumnIndexOrThrow("name");
                int imageTypeColumnIndex = cursor.getColumnIndexOrThrow("image_type");

                int id = cursor.getInt(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String imageType = cursor.getString(imageTypeColumnIndex);
                collectionList.add(new Collection(id, name, imageType)); // Add to the list
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close(); // Always close the cursor
        }
        adapter.notifyDataSetChanged(); // Tell the adapter the data has changed
    }

    // Method to show the dialog for creating a new collection
    private void showAddCollectionDialog() {
        // --- Corrected Layout Resource for Image Selection Dialog ---
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_dialog_image_selection, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        ImageView lofiImage = dialogView.findViewById(R.id.lofi_image);
        ImageView podcastImage = dialogView.findViewById(R.id.podcast_image);
        TextInputEditText nameInput = dialogView.findViewById(R.id.collection_name_input);

        final String[] selectedImageType = {null};
        // TODO: Add visual feedback when an image is selected (e.g., highlight border)
        lofiImage.setOnClickListener(v -> selectedImageType[0] = "lofi");
        podcastImage.setOnClickListener(v -> selectedImageType[0] = "podcast");

        // Set the action for the "Save" button
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            if (!name.isEmpty() && selectedImageType[0] != null) {
                // Call addCollection and get the ID of the new collection
                long newCollectionId = dbHelper.addCollection(name, selectedImageType[0]);

                if (newCollectionId != -1) { // Check if the insertion was successful
                    Toast.makeText(getContext(), "Collection '" + name + "' saved!", Toast.LENGTH_SHORT).show();

                    // --- After successfully saving, IMMEDIATELY show the music selection dialog ---
                    showMusicSelectionDialog((int) newCollectionId); // Pass the ID of the new collection

                    loadCollections(); // Refresh the main list of collections in this fragment
                } else {
                    Toast.makeText(getContext(), "Failed to save collection.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter a name and select an image.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the action for the "Cancel" button
        builder.setNegativeButton("Cancel", null); // null listener just dismisses the dialog

        // Create and show the AlertDialog
        builder.create().show();
    }

    // Method called when an existing collection item is clicked
    private void onCollectionClick(Collection collection) {
        // TODO: Implement what happens when a collection is clicked.
        // You likely want to navigate to a new screen (Activity or Fragment)
        // to display the music *inside* this specific collection.
        // Pass the collection.id to that new screen.

        // Example of showing the music selection dialog again (maybe not the desired flow):
        // showMusicSelectionDialog(collection.id);

        // Example of navigating to a detail screen (recommended):
        // Bundle bundle = new Bundle();
        // bundle.putInt("collection_id", collection.id);
        // CollectionDetailFragment detailFragment = new CollectionDetailFragment(); // Your fragment to show music in a collection
        // detailFragment.setArguments(bundle);
        // getParentFragmentManager().beginTransaction()
        //         .replace(R.id.fragment_container, detailFragment) // Replace R.id.fragment_container with your container ID
        //         .addToBackStack(null)
        //         .commit();
        Toast.makeText(getContext(), "Clicked on collection: " + collection.name, Toast.LENGTH_SHORT).show();
    }

    // Method to show the dialog for selecting music to add to a collection
    private void showMusicSelectionDialog(int collectionId) {
        // Inflate a custom view with a RecyclerView for music selection
        // --- Corrected Layout Resource for Music Selection Dialog ---
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item_collection, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        // --- Corrected RecyclerView ID ---
        RecyclerView musicRecyclerView = dialogView.findViewById(R.id.collection_field);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- GET THE MUSIC LIST DIRECTLY FROM YOUR STATIC VARIABLE IN MainActivity ---
        List<MusicFiles> allMusicFiles = new ArrayList<>(); // Initialize an empty list
        // Check if the static list in MainActivity has been populated
        if (MainActivity.musicFiles != null && !MainActivity.musicFiles.isEmpty()) {
            allMusicFiles.addAll(MainActivity.musicFiles); // Add all files from the static list
        } else {
            // Handle the case where the music files haven't been loaded yet (e.g., permissions not granted or load failed)
            Toast.makeText(getContext(), "Music files not loaded yet. Please grant permissions in Home tab.", Toast.LENGTH_LONG).show();
            // You might want to return or disable the dialog if the list is empty
            return; // Exit the method if music list can't be loaded
        }
        // --- END OF GETTING MUSIC LIST ---


        // Create an adapter with a click listener to handle music selection
        // Ensure your MusicAdapter has the OnMusicClickListener interface and constructor
        MusicAdapter musicAdapter = new MusicAdapter(getContext(), allMusicFiles, new MusicAdapter.OnMusicClickListener() {
            @Override
            public void onMusicClick(MusicFiles musicFile) {
                // --- Call addMusicToCollection and check the result (long) ---
                long result = dbHelper.addMusicToCollection(collectionId, musicFile.getPath());

                if (result != -1) {
                    // Insertion was successful
                    Toast.makeText(getContext(), "Added '" + musicFile.getTitle() + "' to collection.", Toast.LENGTH_SHORT).show();

                    // TODO: If you want to select multiple songs, do NOT dismiss the dialog here.
                    // If you only allow single selection per click, dismiss the dialog.
                    // You would need a reference to the AlertDialog instance.
                    // For example: dialog.dismiss();
                } else {
                    // Insertion failed (e.g., database error)
                    // Consider if the same song should be allowed multiple times or check for duplicates first
                    Toast.makeText(getContext(), "Failed to add music to collection (might already be added).", Toast.LENGTH_SHORT).show();
                }
                // Note: This click listener is for adding music to the collection.
                // It does NOT typically start playback from this dialog.
            }
        });

        musicRecyclerView.setAdapter(musicAdapter);

        // Show the dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Select music to add")
                .setView(dialogView)
                .setNegativeButton("Close", null) // "Close" button just dismisses the dialog
                .create();

        dialog.show();
    }

    // TODO: Remember to close your database helper when the fragment is destroyed
    // Or manage its lifecycle based on your application's needs (e.g., using a ViewModel)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}