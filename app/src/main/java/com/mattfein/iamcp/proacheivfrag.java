package com.mattfein.iamcp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mattfein.iamcp.adapters.AchievementAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class proacheivfrag extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    String fbUserEmail;
    private static final String ACHIEVMENTS = "UserAchievments";
    SwipeRefreshLayout refreshLayout;

    public proacheivfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.acheivmenthistorytab, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        assert fbUser != null;
        fbUserEmail = fbUser.getEmail();
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        setUpAchievments(view, fbUserEmail);
        setUpRefresh(view, fbUserEmail);
        return view;
    }

    private void setUpRefresh(View view, String fbUserEmail){
        refreshLayout.setOnRefreshListener(() -> setUpAchievments(view, fbUserEmail));

    }
    private void setUpAchievments(View view, String fbUserEmail) {

        Log.d("Profile", "Setting Up Achievments");

        DocumentReference query = db.collection("Task").document(fbUserEmail);

        query.get().addOnSuccessListener(documentSnapshot -> {
            List<String> mAcheivNames = (List<String>) documentSnapshot.get(ACHIEVMENTS);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            RecyclerView achievmentRecycle = view.findViewById(R.id.achievmentRecycle);
            achievmentRecycle.setLayoutManager(layoutManager);
            imageQuery(mAcheivNames, achievmentRecycle);
            Log.e("AchievNames", mAcheivNames.toString());
        });
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }

    }

    private void imageQuery(List<String> achievNames, RecyclerView achievRecycle){

        DocumentReference imageQuery = db.collection("AchievmentImages").document("AchievmentImages");
        imageQuery.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> mAcheivLinks = new ArrayList<>();
                for(int i = 0; i < achievNames.size(); i++){
                    if(achievNames.get(i).equals("Registered!")){
                        mAcheivLinks.add((String) documentSnapshot.get("Registered"));
                    }

                }
                AchievementAdapter achievementAdapter = new AchievementAdapter(getContext(), achievNames, mAcheivLinks);
                achievRecycle.setAdapter(achievementAdapter);
            }

        });

    }

}
