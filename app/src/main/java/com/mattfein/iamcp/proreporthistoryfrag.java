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
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mattfein.iamcp.adapters.ProfileAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class proreporthistoryfrag extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    FirebaseAuth mAuth;
    FirebaseUser fbUser;
    String fbUserEmail;
    RecyclerView proNews;

    private static final String ISSUE_AREA_News = "IssueAreas";
    private static final String Names = "Names";
    private static final String LI_LINK = "linkedinPro";
    private static final String ActivityDescriptions_NEWS = "ActivityDescriptions";
    private static final String EXTRA_DETAILS = "taskDescription";
    private static final String TIMESTAMP = "TimeStamps";
    private static final String ISSUE_AREA = "issueArea";
    private static final String activityType = "activityType";
    SwipeRefreshLayout refreshLayout;
    Boolean isRefreshing = false;

    public proreporthistoryfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reporthistorytab, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        fbUserEmail = fbUser.getEmail();
        proNews = view.findViewById(R.id.profileRecycle);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        setUpFeed();
        setUpRefresh(view);

        return view;
    }

    private void setUpRefresh(View view) {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpFeed();
            }
        });

    }

    private void setUpFeed() {
        DocumentReference query = db.collection("Task").document(fbUserEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> currentTask;
                List<String> currentType;
                String currentProLink;
                List<String> currentDates;
                String currentNames;
                List<String> currentExtraDetails;

                currentTask = (List<String>) documentSnapshot.get(ISSUE_AREA);
                if(currentTask != null){
                    currentTask = Lists.reverse(currentTask);
                }

                currentProLink = (String) documentSnapshot.get(LI_LINK);
                currentDates = (List<String>) documentSnapshot.get(TIMESTAMP);
                if(currentDates != null){
                    currentDates = Lists.reverse(currentDates);
                }
                currentNames = (String) documentSnapshot.get(Names);
                currentExtraDetails = (List<String>) documentSnapshot.get(EXTRA_DETAILS);
                String sizeofArray = Integer.toString(currentExtraDetails.size());
                Log.e("Extra Details Length", sizeofArray);
                if(currentExtraDetails != null){
                    currentExtraDetails = Lists.reverse(currentExtraDetails);
                }


                Log.e("Currenttask", currentTask.toString());
                currentType = (List<String>) documentSnapshot.get(activityType);
                currentType = Lists.reverse(currentType);
                initRecyclerView(currentTask, currentType, currentProLink, currentDates, currentNames, currentExtraDetails);

            }
        });
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }

    }

    private void initRecyclerView(List<String> currentTask, List<String> currentType, String currentProLink,List<String> currentDates, String currentNames, List<String> currentExtraDetails){
        ProfileAdapter adapter = new ProfileAdapter(getContext(), currentType, currentTask, currentProLink, currentDates, currentNames, currentExtraDetails);
        proNews.setAdapter(adapter);
        proNews.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
