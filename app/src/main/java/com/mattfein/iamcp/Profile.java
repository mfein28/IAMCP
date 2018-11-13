package com.mattfein.iamcp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mattfein.iamcp.adapters.AchievementAdapter;
import com.mattfein.iamcp.adapters.ProfileAdapter;
import com.mattfein.iamcp.adapters.SectionsPageAdapter;
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
    FirebaseAuth mAuth;
    Map.Entry<String, Integer> mainInterest = null;
    int taskCount;

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;


    private static final String ISSUE_AREA = "issueArea";
    private static final String ORGANIZATION_NAME = "BusinessName";
    private static final String activityType = "activityType";
    private static final String POINT_VALUE = "pointValue";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
   // final String liLink, currentName, organizationName, stringtaskCount;


    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        setUpUIElements(view);
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        fbUserEmail = fbUser.getEmail();
        ConstraintLayout frame = view.findViewById(R.id.frameprof);
        frame.setVisibility(View.VISIBLE);
        setUpUserProfile(view, fbUserEmail);
        mSectionsPageAdapter = new SectionsPageAdapter(getActivity().getSupportFragmentManager());
        mViewPager = view.findViewById(R.id.viewPager);
        setUpViewPager(mViewPager);
        assert mViewPager != null;
        TabLayout tabLayout = view.findViewById(R.id.profiletabs);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }


    private void setUpViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new proreporthistoryfrag(), "Report History");
        adapter.addFragment(new proacheivfrag(), "Achievements");
        viewPager.setAdapter(adapter);
    }





    public void setUpUIElements(View view) {
        name =  view.findViewById(R.id.usersname);
        organization = view.findViewById(R.id.organization_name);
        primaryConcern =  view.findViewById(R.id.primary_policy_concern_title);
        numTasks =  view.findViewById(R.id.numbersubited);
        proPic =  view.findViewById(R.id.profileImage);
    }

    public void setUpUserProfile(View view, String userEmail){

        name =  view.findViewById(R.id.usersname);
        organization =  view.findViewById(R.id.organization_name);
        primaryConcern =  view.findViewById(R.id.primary_policy_concern_title);
        numTasks =  view.findViewById(R.id.numbersubited);
        proPic =  view.findViewById(R.id.profileImage);
        usersScore =  view.findViewById(R.id.usersScore);
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
