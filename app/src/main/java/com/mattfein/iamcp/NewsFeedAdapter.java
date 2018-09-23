package com.mattfein.iamcp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private List<String> mNames = new ArrayList<>();
    private List<String> mIssueArea = new ArrayList<>();
    private List<String> mactivityDescription = new ArrayList<>();
    private List<String> mProPicLink = new ArrayList<>();
    private Context mContext;


    public NewsFeedAdapter(Context context, List<String> Names, List<String> IssueAreas, List<String> ActivityDescription, List<String> ProPicLink){
        mNames = Names;
        mIssueArea = IssueAreas;
        mactivityDescription = ActivityDescription;
        mProPicLink = ProPicLink;
        mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeedlistitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(mProPicLink.get(position)).into(holder.proPic);
        holder.userName.setText(mNames.get(position));
        holder.recyclerIssueArea.setText(mIssueArea.get(position));
        holder.activityDescription.setText(mactivityDescription.get(position));


        //Set onClickListener Here

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView proPic;
        RelativeLayout parentLayout;
        TextView userName, recyclerIssueArea, activityDescription;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            proPic = itemView.findViewById(R.id.recyclerviewImage);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            userName = itemView.findViewById(R.id.usersname);
            recyclerIssueArea = itemView.findViewById(R.id.issueArearecycle);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            cardView = itemView.findViewById(R.id.cardView);

        }




    }
}
