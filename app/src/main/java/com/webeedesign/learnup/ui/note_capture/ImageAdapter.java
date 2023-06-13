package com.webeedesign.learnup.ui.note_capture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.webeedesign.learnup.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final Context context;

    private final ArrayList<Uri> originalList;
    private final ArrayList<Uri> imageUriList;

    public ImageAdapter(Context context, ArrayList<Uri> originalList, String prefix) {
        this.context = context;
        this.imageUriList = new ArrayList<>();
        for (Uri uri : originalList) {
            if (uri.getLastPathSegment().startsWith(prefix)) {
                imageUriList.add(uri);
            }
        }
        this.originalList = originalList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }


    //populate recycle view using glide library
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = String.valueOf(imageUriList.get(position));
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.raw.loading)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                Uri imageUri = originalList.get(position);

                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("image_url", imageUri.toString());
                context.startActivity(intent);
            }
        }

    }
}
