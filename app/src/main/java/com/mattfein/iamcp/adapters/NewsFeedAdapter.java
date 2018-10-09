package com.mattfein.iamcp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mattfein.iamcp.R;
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
    View view;


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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeedlistitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(mProPicLink.get(position)).into(holder.proPic);
        holder.userName.setText(mNames.get(position));
        holder.recyclerIssueArea.setText(mIssueArea.get(position));
        holder.activityDescription.setText(mactivityDescription.get(position));
        CardView cardView = view.findViewById(R.id.cardView);
        setAnimation(cardView, position);


        //Set onClickListener Here

    }

    private void setAnimation(CardView cardView, int position) {

        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left );
        cardView.startAnimation(animation);

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
