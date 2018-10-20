package com.mattfein.iamcp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mattfein.iamcp.adapters.ProfileAdapter;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    FirebaseAuth firebaseAuth;
    TextView name, organization, primaryConcern, numTasks, usersScore;
    CircleImageView proPic;
    String fbUserEmail;
    FirebaseUser fbUser;
    String stringScore;
    private static final String ISSUE_AREA = "issueArea";
    private static final String ORGANIZATION_NAME = "BusinessName";
    private static final String activityType = "activityType";
    private static final String POINT_VALUE = "pointValue";
    FirebaseAuth mAuth;
    RecyclerView proNews;
    Map.Entry<String, Integer> mainInterest = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
   // final String liLink, currentName, organizationName, stringtaskCount;
    int taskCount;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        setUpUIElements(view);
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        fbUserEmail = fbUser.getEmail();
        FrameLayout frame = view.findViewById(R.id.frameProf);
        frame.setVisibility(View.VISIBLE);
        proNews = (RecyclerView) view.findViewById(R.id.profileRecycle);
        setUpFeed();
        setUpUserProfile(view, fbUserEmail);
        return view;
    }

    private void setUpFeed() {
        DocumentReference query = db.collection("Task").document(fbUserEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> currentTask = new ArrayList<>();
                List<String> currentType = new ArrayList<>();
                currentTask = (List<String>) documentSnapshot.get(ISSUE_AREA);
                currentTask = Lists.reverse(currentTask);
                Log.e("Currenttask", currentTask.toString());
                currentType = (List<String>) documentSnapshot.get(activityType);
                currentType = Lists.reverse(currentType);
                initRecyclerView(currentTask, currentType);

            }
        });

    }

    private void initRecyclerView(List<String> currentTask, List<String> currentType){
        ProfileAdapter adapter = new ProfileAdapter(getContext(), currentType, currentTask);
        proNews.setAdapter(adapter);
        proNews.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    public void setUpUIElements(View view) {
        name = (TextView) view.findViewById(R.id.usersname);
        organization = (TextView) view.findViewById(R.id.organization_name);
        primaryConcern = (TextView) view.findViewById(R.id.primary_policy_concern_title);
        numTasks = (TextView) view.findViewById(R.id.numbersubited);
        proPic = (CircleImageView) view.findViewById(R.id.profileImage);
    }

    public void setUpUserProfile(View view, String userEmail){

        name = (TextView) view.findViewById(R.id.usersname);
        organization = (TextView) view.findViewById(R.id.organization_name);
        primaryConcern = (TextView) view.findViewById(R.id.primary_policy_concern_title);
        numTasks = (TextView) view.findViewById(R.id.numbersubited);
        proPic = (CircleImageView) view.findViewById(R.id.profileImage);
        usersScore = (TextView) view.findViewById(R.id.usersScore);
        DocumentReference user = db.collection("Task").document(userEmail);
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 String liLink = documentSnapshot.getString("linkedinPro");
                 String organizationName = documentSnapshot.getString(ORGANIZATION_NAME);
                 String currentName = documentSnapshot.getString("Name");
                 Long currentUserScore = (Long) documentSnapshot.get(POINT_VALUE);
                 if(currentUserScore !=  null){
                     stringScore = Long.toString(currentUserScore);
                 }
                 else{
                     stringScore = "0";
                 }

                 List<String> taskList = new ArrayList<String>();
                 if(documentSnapshot.get(ISSUE_AREA) instanceof String){

                     taskList.add(documentSnapshot.get(ISSUE_AREA).toString());
                 }
                 else{
                        taskList = (List<String>) documentSnapshot.get(ISSUE_AREA);
                 }

                 try{
                     taskCount = taskList.size();
                     HashMap<String, Integer> mostCommon = new HashMap<>();
                     for(int i = 0; i < taskList.size(); i++){
                            if(mostCommon.containsKey(taskList.get(i))){
                                mostCommon.put(taskList.get(i), mostCommon.get(taskList.get(i)+ 1));
                         }
                         else{
                                mostCommon.put(taskList.get(i), 1);
                            }
                     }

                     for(Map.Entry<String, Integer> entry: mostCommon.entrySet()){
                         if(mainInterest == null || entry.getValue().compareTo(mainInterest.getValue()) > 0){
                             mainInterest = entry;
                         }
                     }
                 }
                 catch(NullPointerException e){
                     numTasks.setText("0");
                     Log.e("Task Count", "Error");
                 }

                 String mainInterestString;
                 if(mainInterest != null){
                     mainInterestString = mainInterest.getKey();
                 }
                 else{
                     mainInterestString = "No Reports Submitted";
                 }

                 final String stringtaskCount = Integer.toString(taskCount);
                 Log.e("This is task:", stringtaskCount);
                 Log.e("This is userName:", currentName);
                 name.setText(currentName);
                 organization.setText(organizationName);
                 numTasks.setText(stringtaskCount);
                 usersScore.setText(stringScore);
                 primaryConcern.setText(mainInterestString);
                 Picasso.get().load(liLink).into(proPic);

            }
        });



    }
}
