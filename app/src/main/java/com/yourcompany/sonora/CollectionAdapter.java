package com.yourcompany.sonora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private List<Collection> collections;
    private Context context;
    private OnCollectionClickListener listener;

    public interface OnCollectionClickListener {
        void onCollectionClick(Collection collection);
    }

    public CollectionAdapter(Context context, List<Collection> collections, OnCollectionClickListener listener) {
        this.context = context;
        this.collections = collections;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_collection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Collection collection = collections.get(position);
        holder.name.setText(collection.name);

        // Set the appropriate image based on the collection type
        if ("lofi".equals(collection.imageType)) {
            holder.image.setImageResource(R.drawable.lofi_image);
        } else {
            holder.image.setImageResource(R.drawable.podcast_image);
        }

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCollectionClick(collection);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.collection_image);
            name = itemView.findViewById(R.id.collection_name);
        }
    }
}