package com.mattfein.iamcp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mattfein.iamcp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {
    private static final String TAG = "AchievmentAdapter";
    private List<String> mText = new ArrayList<String>();
    private List<String> mImages = new ArrayList<String>();
    private Context mContext;

    public AchievementAdapter(Context mContext, List<String> mText, List<String> mImages) {
        this.mText = mText;
        this.mImages = mImages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "Got here");
        Log.e(TAG, mText.toString());
        Log.e(TAG, mImages.toString());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievment_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.achievText.setText(mText.get(position));
        Picasso.get().load(mImages.get(position)).into(holder.achievImage);
        holder.achievImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mText.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView achievImage;
//        TextView achievText;

        public ViewHolder(View itemView) {
            super(itemView);
            achievImage = itemView.findViewById(R.id.achievementImage);
//            achievText = itemView.findViewById(R.id.achievmentText);
        }
    }
}
