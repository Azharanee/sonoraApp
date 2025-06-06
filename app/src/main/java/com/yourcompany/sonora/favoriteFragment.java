package com.yourcompany.sonora;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link favoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class favoriteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public favoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment favoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private ArrayList<MusicFiles> favoriteList = new ArrayList<>();
    public static favoriteFragment newInstance(String param1, String param2) {
        favoriteFragment fragment = new favoriteFragment();
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


//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_favorite, container, false);
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.favorite_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Use the main music list from MainActivity
        ArrayList<MusicFiles> allMusicFiles = MainActivity.musicFiles;

        // Filter only favorites
        favoriteList.clear();
        if (allMusicFiles != null) {
            for (MusicFiles music : allMusicFiles) {
                if (music.isFavorite()) {
                    favoriteList.add(music);
                }
            }
        }

        // Set up the adapter
        musicAdapter = new MusicAdapter(getContext(), favoriteList);
        recyclerView.setAdapter(musicAdapter);

        return view;
    }
}