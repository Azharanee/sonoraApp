package com.yourcompany.sonora;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class homeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;

    // Empty constructor
    public homeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        // Initialize with empty list if null
        if (MainActivity.musicFiles == null) {
            MainActivity.musicFiles = new ArrayList<>();
            Toast.makeText(getContext(), "No songs found", Toast.LENGTH_SHORT).show();
        }

        try {
            // Safe initialization of adapter
            musicAdapter = new MusicAdapter(getContext(), MainActivity.musicFiles);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    RecyclerView.VERTICAL, false));

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading music", Toast.LENGTH_SHORT).show();
            Log.e("homeFragment", "Adapter initialization failed", e);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh adapter if needed
        if (musicAdapter != null) {
            musicAdapter.notifyDataSetChanged();
        }
    }
}