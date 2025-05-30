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
import java.util.List; // <--- ADD THIS IMPORT

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1;

    // Your original constructor
    MusicAdapter(Context mContext,ArrayList<MusicFiles>mFiles)
    {
        this.mFiles = mFiles;
        this.mContext =mContext;

    }

    //////////////new
    // This field is already here, good.
    private OnMusicClickListener listener;

    // This interface is already here, good.
    public interface OnMusicClickListener {
        void onMusicClick(MusicFiles musicFile);
    }
    ///////////

    // <--- ADD THE NEW CONSTRUCTOR HERE --->
    public MusicAdapter(Context mContext, List<MusicFiles> mFiles, OnMusicClickListener listener) {
        this.mContext = mContext;
        // Convert the List to an ArrayList to match your existing field type
        this.mFiles = new ArrayList<>(mFiles);
        this.listener = listener; // Assign the listener
    }
    // <--- END OF NEW CONSTRUCTOR --->


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
        image = getAlbumart(mFiles.get(position).getPath());
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

        // Handle play_icon click (playback logic) - KEEP this as it is for play/pause
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

        // Handle favorite_icon click - KEEP this as it is
        if (mFiles.get(position).isFavorite()) {
            holder.favorite_icon.setImageResource(R.drawable.red_heart); // Use your filled heart drawable
        } else {
            holder.favorite_icon.setImageResource(R.drawable.ic_favorite); // Use your empty heart drawable
        }

        holder.favorite_icon.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            boolean newState = !mFiles.get(pos).isFavorite();
            mFiles.get(pos).setFavorite(newState);
            notifyItemChanged(pos);
        });

        // <--- REPLACE YOUR EXISTING holder.itemView.setOnClickListener BLOCK WITH THIS --->
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            // Check if a listener was provided (used in the music selection dialog)
            if (listener != null) {
                // If the listener is present, call its method.
                // In the music selection dialog, this listener will add to the collection.
                listener.onMusicClick(mFiles.get(pos));

                // Optionally, close the dialog after selection if it's a single selection dialog.
                // If you keep the dialog open for multiple selections, don't dismiss here.
                // If clicking in the dialog should NOT start the play activity,
                // add a return here or another flag.

            } else {
                // If no listener is provided (used in your main music list for playback)
                // do the default action which is starting the activity_play.
                Intent intent = new Intent(mContext, activity_play.class);
                intent.putExtra("position", pos);
                // If your activity_play needs the list, pass it:
                // intent.putExtra("musicList", mFiles); // Make MusicFiles Parcelable or Serializable
                mContext.startActivity(intent);
            }
        });
        // <--- END OF REPLACED BLOCK --->
    }

    @Override
    public int getItemCount() {return mFiles.size();}

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView album_img;
        ImageView play_icon;
        ImageView favorite_icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_name);
            album_img =itemView.findViewById(R.id.music_img);
            play_icon = itemView.findViewById(R.id.play_icon); // Make sure these IDs are in music_item.xml
            favorite_icon = itemView.findViewById(R.id.favorite_icon); // Make sure these IDs are in music_item.xml
        }
    }

    // Modify getAlbumart to handle potential exceptions gracefully
    private byte [] getAlbumart (String uri) { // Removed throws IOException from signature
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] art = null;
        try {
            retriever.setDataSource(uri);
            art = retriever.getEmbeddedPicture();
        } catch (RuntimeException e) {
            // Log error when setting data source fails
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) { // Changed to IOException
                e.printStackTrace(); // Log error when releasing
            }
        }
        return art;
    }

    // Optional: Add a method to update the list and notify changes
    public void updateData(List<MusicFiles> newFiles) {
        this.mFiles.clear();
        this.mFiles.addAll(newFiles);
        notifyDataSetChanged();
    }
}