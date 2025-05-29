package com.yourcompany.sonora;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.core.content.ContextCompat;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yourcompany.sonora.databinding.ActivityMainBinding;




import com.yourcompany.sonora.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

public static final int REQUEST_CODE =1;
static ArrayList<MusicFiles> musicFiles;
    @Override
             protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
///
        ////
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new homeFragment());
            } else if (id == R.id.collection) {
                replaceFragment(new collectionFragment());
            } else if (id == R.id.favorite) {
                replaceFragment(new favoriteFragment());
            }
            return true;
        });

        // Edge-to-edge handling
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        permission();

    }



    // Method to replace fragments
    private void replaceFragment(Fragment fragment) {
        // Begin fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Replace fragment in the FrameLayout
        fragmentTransaction.replace(R.id.frameLayout, fragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }
//    private void permission() {
//        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_MEDIA_AUDIO)!= PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_MEDIA_AUDIO},REQUEST_CODE);
//        }
//     else {
//
//            musicFiles = getAllAudio(this);
//        }
//        }
    private void permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
            } else {
                musicFiles = getAllAudio(this);
                Log.d("MUSIC_COUNT", "Loaded songs: " + musicFiles.size());


            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                musicFiles = getAllAudio(this);
                Log.d("MUSIC_COUNT", "Loaded songs: " + musicFiles.size());

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

musicFiles =getAllAudio(this);
                Log.d("MUSIC_COUNT", "Loaded songs: " + musicFiles.size());

            }
            else
            {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                }

            }
        }

        }
        public static ArrayList<MusicFiles> getAllAudio ( Context context )
        {
            ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE


            };
//            Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");

        if(cursor != null) {
                while (cursor.moveToNext()) {
                    String album = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String duration = cursor.getString(2);
                    String path = cursor.getString(3);
                    String title = cursor.getString(4);
                    if (path == null || title == null || path.trim().isEmpty()) continue;

                    MusicFiles musicFile = new MusicFiles(album, artist, duration, path, title);
                    tempAudioList.add(musicFile);
//                    MusicFiles musicFile = new MusicFiles(album, artist, duration, path, title);
//                    Log.e("Path: " + path, "Album: " + album);
//                    tempAudioList.add(musicFile);
                }

            cursor.close();
        }
        return tempAudioList;
    }
}
