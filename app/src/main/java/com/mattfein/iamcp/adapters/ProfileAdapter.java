package com.mattfein.iamcp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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


public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private View view;
    private Context mContext;
    private List<String> mIssueAreas;
    private List<String> mActivityDescription;
    private String mProPicLink;
    private List<String> mDates;
    private String mNames;
    private List<String> mExtraDetails;

    public ProfileAdapter(Context context, List<String> issueAreas, List<String> activityDescription, String ProPicLink, List<String> dates, String name, List<String> ExtraDetails) {
        mContext = context;
        mIssueAreas = issueAreas;
        mActivityDescription = activityDescription;
        mProPicLink = ProPicLink;
        mExtraDetails = ExtraDetails;
        mDates = dates;
        mNames = name;
        mExtraDetails = ExtraDetails;

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
        holder.protimeStampText.setText(mDates.get(position));
        Picasso.get().load(mProPicLink).into(holder.recyclerViewImage);
        setAnimation(cardView, position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("We clicked", "woop");
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                final View newsPopup = inflater.inflate(R.layout.newsfeedalert, null);
                CircleImageView proPic = newsPopup.findViewById(R.id.popupPro);
                TextView popUpName = newsPopup.findViewById(R.id.usersname);
                TextView popUpDate = newsPopup.findViewById(R.id.alertDate);
                TextView issueAreaPop = newsPopup.findViewById(R.id.issueArearecyclepopUp);
                TextView activityDescPop = newsPopup.findViewById(R.id.activityDescriptionPop);
                TextView extraDetailsPop = newsPopup.findViewById(R.id.extraDetailsText);
                Picasso.get().load(mProPicLink).into(proPic);
                popUpName.setText(mNames);
                issueAreaPop.setText(mIssueAreas.get(position));
                activityDescPop.setText(mActivityDescription.get(position));
                if(mExtraDetails == null){
                  extraDetailsPop.setText(" ");
                }
                else{
                    extraDetailsPop.setText(mExtraDetails.get(position));
                }
                popUpDate.setText(mDates.get(position));
                alert.setView(newsPopup);
                alert.show();
            }
        });
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
        TextView recyclerIssueArea, activityDescription, protimeStampText;
        CardView cardView;
        CircleImageView recyclerViewImage;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            recyclerIssueArea = itemView.findViewById(R.id.issueArearecycle);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            cardView = itemView.findViewById(R.id.cardView);
            protimeStampText = itemView.findViewById(R.id.newsFeedTimeStamp);
            recyclerViewImage = itemView.findViewById(R.id.recyclerviewImage);

        }




    }
}






