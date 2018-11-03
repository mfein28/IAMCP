package com.mattfein.iamcp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mattfein.iamcp.R;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepAdapter extends RecyclerView.Adapter<RepAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mRepPicsList = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mParties = new ArrayList<>();
    private ArrayList<String> mPhone = new ArrayList<>();
    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<Map<String, String>> mChannel = new ArrayList<>();
    private Context mContext;
    private RecyclerView recyclerView;
    int lastPosition;
    View view;
    private static final String REPCOUNT = "isRepRead";
    private String mUserEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RepAdapter(RecyclerView recyclerView, ArrayList<String> URLs, ArrayList<String> RepPicsList, ArrayList<String> Names, ArrayList<String> Parties, ArrayList<String> Phone, ArrayList<Map<String, String>> channel, Context Context, String usersEmail) {
        this.mRepPicsList = RepPicsList;
        this.recyclerView = recyclerView;
        this.mNames = Names;
        this.mParties = Parties;
        this.mPhone = Phone;
        this.mContext = Context;
        this.mUrls = URLs;
        this.mChannel = channel;
        this.mUserEmail = usersEmail;
    }



    @NonNull
    @Override
    public RepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replistitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        iterateRep();
        return holder;
    }

    public void iterateRep(){
        final DocumentReference query = db.collection("Task").document(mUserEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long currentStatus = (Long) documentSnapshot.get(REPCOUNT);
                currentStatus = currentStatus + 1;
                query.update(REPCOUNT, currentStatus);



            }
        });

    }

    @Override
    public void onBindViewHolder(@NonNull RepAdapter.ViewHolder holder, int position) {
        Log.e("This is link", mRepPicsList.get(position));
        try {
            Picasso.get().load(mRepPicsList.get(position)).into(holder.repImage);
        }
        catch (IllegalArgumentException e){
            Picasso.get().load(R.drawable.iamcp);
        }
        holder.phoneNumber.setText(mPhone.get(position));
        holder.name.setText(mNames.get(position));
        String whichParty = mParties.get(position);
        String repUrl = mUrls.get(position);
        Map<String, String> socialChannels = mChannel.get(position);
        Uri twitterLink = null;
        Uri youtubeLink = null;
        Uri representativeURL = null;
        Uri facebookLink = null;
        //Sets fblink
        try{
            String fbID = null;
            fbID = socialChannels.get("Facebook");
            Log.e("Facebook", fbID);
            if(fbID == null){
                holder.facebookLink.setVisibility(View.GONE);
            }
            facebookLink = Uri.parse("https://facebook.com/" + fbID);
            Log.e("FacebookLink", facebookLink.toString());
        }
        catch (NullPointerException e){

        }

        //Sets repUrl
        representativeURL = Uri.parse(repUrl);
        Log.e("Rep URL", repUrl);

        try{
            String youtubeID = null;
            youtubeID = socialChannels.get("YouTube");
            if(youtubeID == null){
                holder.youtubeLink.setVisibility(View.GONE);
            }
            youtubeLink = Uri.parse("https://youtube.com/channel/" + youtubeID);
            Log.e("Youtube", youtubeID);
            Log.e("YoutubeLInk", youtubeLink.toString());
        }
        catch (NullPointerException e){

        }
        try{
            String twitterID = null;
            twitterID = socialChannels.get("Twitter");
            Log.e("Twitter", twitterID);
             twitterLink = Uri.parse("https://twitter.com/" + twitterID);
            if(twitterID == null){
                holder.twitterLink.setVisibility(View.GONE);
            }
            Log.e("TwitterLink", twitterLink.toString());

        }
        catch (NullPointerException e){

        }

        if(whichParty.contains("D")){
            holder.party.setImageResource(R.drawable.democrat);
        }
        if(whichParty.contains("R")){
            holder.party.setImageResource(R.drawable.gop);
        }

        Uri finalTwitterLink = twitterLink;
        Uri finalYoutubeLink = youtubeLink;
        Uri finalFacebookLink = facebookLink;
        Uri finalRepresentativeURL = representativeURL;
        Log.e("FINALURLLINK",  finalRepresentativeURL.toString());
        holder.urlLink.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, finalRepresentativeURL);

                mContext.startActivity(intent);

            }
        });
        holder.twitterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,finalTwitterLink);
                mContext.startActivity(intent);
            }
        });


        holder.youtubeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,finalYoutubeLink);
                mContext.startActivity(intent);
            }
        });


        holder.facebookLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,finalFacebookLink);
                mContext.startActivity(intent);
            }
        });

        CardView cardView = view.findViewById(R.id.cardView);
        setAnimation(cardView, position);
    }

    private void setAnimation(CardView cardView, int position) {

            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left );
            cardView.startAnimation(animation);

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView name, phoneNumber;
        ImageView party;
        ImageView repImage, twitterLink, youtubeLink, facebookLink, urlLink;
        CardView repCard;
        Context holdcontext;

        public ViewHolder(View itemView) {
            super(itemView);
            holdcontext = itemView.getContext();
            repCard = itemView.findViewById(R.id.representativesCard);
            repImage = itemView.findViewById(R.id.repImage);
            name = itemView.findViewById(R.id.repName);
            phoneNumber = itemView.findViewById(R.id.repphone);
            twitterLink = itemView.findViewById(R.id.twitterlink);
            youtubeLink = itemView.findViewById(R.id.youtubelink);
            facebookLink = itemView.findViewById(R.id.facebooklink);
            party = itemView.findViewById(R.id.party);
            urlLink = itemView.findViewById(R.id.urlLink);


        }
    }
}
