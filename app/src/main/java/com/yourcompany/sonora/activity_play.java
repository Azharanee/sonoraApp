package com.yourcompany.sonora;

import static com.yourcompany.sonora.MainActivity.getAllAudio;
import static com.yourcompany.sonora.MainActivity.musicFiles;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class activity_play extends AppCompatActivity {
TextView song_name,artist_name,duration_played,duration_total;
ImageView cover_art,nextBtn,prevBtn,shuffleBtn,reaptBtn;
SeekBar seekBar;
FloatingActionButton playPauseBtn;
int position =-1;
static Uri uri;
static MediaPlayer mediaPlayer;
private Handler handler = new Handler();
private Thread playThread,prevThread,nextThread;
static ArrayList<MusicFiles>listsongs =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);
      /*  ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)*/
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        initViews();
        getIntenMethod();
       song_name.setText(listsongs.get(position).getTitle());
       artist_name.setText(listsongs.get(position).getArtist());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null  && fromUser)
                    mediaPlayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        activity_play.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
               handler.postDelayed(this,1000);
            }
        });
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout="";
        String totalNew="";
        String seconds =String.valueOf(mCurrentPosition %60);
        String minutes =String.valueOf(mCurrentPosition /60);
        totalout =minutes +":"+ seconds;
        totalNew= minutes +":"+ "0" + seconds;
        if(seconds.length()==1)
        {
            return totalNew;
        }
        else
        {
            return totalout;
        }


    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevBtnBtn();
        super.onResume();
    }

    private void prevBtnBtn() {
        prevThread =new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();

    }

    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position-1) < 0 ? listsongs.size()-1 :(position-1));
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            activity_play.this.runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position-1) < 0 ? listsongs.size()-1 :(position-1));
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            activity_play.this.runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_play);
        }
    }

    private void nextThreadBtn() {
        nextThread =new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();

    }

    private void nextBtnClicked() {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position+1)% listsongs.size());
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            activity_play.this.runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_pause);
          mediaPlayer.start();
        }
     else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position+1)% listsongs.size());
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            activity_play.this.runOnUiThread(new Runnable() {
                // @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_play);
        }
    }

    private void playThreadBtn() {
        playThread =new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       playPauseBtnClicked();
                    }
                });
            }
        };
       playThread.start();
    }

    private void playPauseBtnClicked() {
if (mediaPlayer.isPlaying())
{
  playPauseBtn.setImageResource(R.drawable.ic_play);
  mediaPlayer.pause();
  seekBar.setMax(mediaPlayer.getDuration()/1000);
    activity_play.this.runOnUiThread(new Runnable() {
       // @Override
        public void run() {
            if(mediaPlayer != null)
            {
                int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                seekBar.setProgress(mCurrentPosition);
                duration_played.setText(formattedTime(mCurrentPosition));
            }
            handler.postDelayed(this,1000);
        }
    });
}
else
{
    playPauseBtn.setImageResource(R.drawable.ic_pause);
    mediaPlayer.start();
    seekBar.setMax(mediaPlayer.getDuration()/1000);
    activity_play.this.runOnUiThread(new Runnable() {
        // @Override
        public void run() {
            if(mediaPlayer != null)
            {
                int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                seekBar.setProgress(mCurrentPosition);
                duration_played.setText(formattedTime(mCurrentPosition));
            }
            handler.postDelayed(this,1000);
        }
    });
}
    }

    private void getIntenMethod() {
        position =getIntent().getIntExtra("position",-1);
        listsongs =musicFiles;
        if (listsongs!= null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listsongs.get(position).getPath());
            if (mediaPlayer !=null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
               mediaPlayer =MediaPlayer.create(getApplicationContext(),uri);
               mediaPlayer.start();
            }
            else
            {
                mediaPlayer =MediaPlayer.create(getApplicationContext(),uri);
             mediaPlayer.start();
            }
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            metaData(uri);
        }

    }

    private void initViews() {
        song_name =findViewById(R.id.song_name);
        artist_name =findViewById(R.id.song_artist);
        duration_played =findViewById(R.id.duration_Played);
        duration_total =findViewById(R.id.duration_Total);
        cover_art =findViewById(R.id.cover_art);
        nextBtn =findViewById(R.id.id_next);
        prevBtn =findViewById(R.id.id_prev);
        reaptBtn =findViewById(R.id. repeat);
        shuffleBtn =findViewById(R.id. shuffle);
        playPauseBtn =findViewById(R.id. play_pause);
        seekBar =findViewById(R.id. seekBar);


    }
    private void metaData (Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal =Integer.parseInt(listsongs.get(position).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art =retriever.getEmbeddedPicture();
        if(art !=null)
        {
            Glide.with(this).asBitmap().load(art).into(cover_art);
        }
        else
        {
            Glide.with(this).asBitmap().load(R.drawable.empty).into(cover_art);
        }

    }

}