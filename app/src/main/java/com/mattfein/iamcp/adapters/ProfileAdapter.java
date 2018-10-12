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


import java.util.ArrayList;
import java.util.List;




public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    View view;
    private Context mContext;
    private List<String> mIssueAreas = new ArrayList<>();
    private List<String> mActivityDescription = new ArrayList<>();

    public ProfileAdapter(Context context, List<String> issueAreas, List<String> activityDescription) {
        mContext = context;
        mIssueAreas = issueAreas;
        mActivityDescription = activityDescription;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profilelistitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        holder.activityDescription.setText(mActivityDescription.get(position));
        holder.recyclerIssueArea.setText(mIssueAreas.get(position));
        CardView cardView = view.findViewById(R.id.cardView);
        if(position % 3 == 0){
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.gray));
        }
        if(position % 3 == 1){
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorLight));
        }
        if(position % 3 == 2){
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        }

        setAnimation(cardView, position);
    }

    private void setAnimation(CardView cardView, int position) {

        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left );
        cardView.startAnimation(animation);

    }


    @Override
    public int getItemCount() {
        return mActivityDescription.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parentLayout;
        TextView recyclerIssueArea, activityDescription;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            recyclerIssueArea = itemView.findViewById(R.id.issueArearecycle);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            cardView = itemView.findViewById(R.id.cardView);

        }




    }
}






