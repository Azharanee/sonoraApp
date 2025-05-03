package com.yourcompany.sonora;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

   private Context mContext;
   private ArrayList<MusicFiles> mFiles;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1;
   MusicAdapter(Context mContext,ArrayList<MusicFiles>mFiles)
   {
this.mFiles = mFiles;
this.mContext =mContext;

   }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.music_item,parent,false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder .file_name.setText(mFiles.get(position).getTitle());
        byte[] image= null;
        try {
            image = getAlbumart(mFiles.get(position).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if( image !=null)
       {
           Glide.with(mContext).asBitmap()
                   .load(image)
                   .into(holder.album_img);
       }
       else {

           Glide.with(mContext)
                   .load(R.drawable.empty)
                   .into(holder.album_img);
       }
        if (position == playingPosition) {
            holder.play_icon.setImageResource(R.drawable.ic_pause);
        } else {
            holder.play_icon.setImageResource(R.drawable.ic_play);
        }
        holder.play_icon.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            if (playingPosition == pos) {
                playingPosition = -1;
                notifyDataSetChanged();
                return;
            }

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mFiles.get(pos).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                playingPosition = pos;
                notifyDataSetChanged();

                mediaPlayer.setOnCompletionListener(mp -> {
                    playingPosition = -1;
                    notifyDataSetChanged();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent= new Intent(mContext, activity_play.class);
        mContext.startActivity(intent);
    }
});
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, activity_play.class);
//            intent.putExtra("songPath", mFiles.get(position).getPath());
//            mContext.startActivity(intent);
//        });

    }

    @Override
    public int getItemCount() {return mFiles.size();}

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
TextView file_name;
ImageView album_img;
ImageView play_icon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_name);
            album_img =itemView.findViewById(R.id.music_img);
            play_icon = itemView.findViewById(R.id.play_icon);
        }
    }
    private byte [] getAlbumart (String uri) throws IOException {
        MediaMetadataRetriever retriver = new MediaMetadataRetriever();
        retriver.setDataSource(uri);
        byte[] art =retriver.getEmbeddedPicture();
        try {
            retriver.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;
    }
}
