package com.mattfein.iamcp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    FirebaseAuth firebaseAuth;
    TextView name, organization, primaryConcern, numTasks;
    CircleImageView proPic;
    String fbUserEmail;
    FirebaseUser fbUser;
    private static final String ISSUE_AREA = "issueArea";
    private static final String ORGANIZATION_NAME = "BusinessName";
    FirebaseAuth mAuth;
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
        setUpUserProfile(view, fbUserEmail);
        return view;
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
        DocumentReference user = db.collection("Task").document(userEmail);
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 String liLink = documentSnapshot.getString("linkedinPro");
                 String organizationName = documentSnapshot.getString(ORGANIZATION_NAME);
                 String currentName = documentSnapshot.getString("Name");
                 final List<String> taskList = (List<String>) documentSnapshot.get(ISSUE_AREA);
                 try{
                     taskCount = taskList.size();
                     final String stringtaskCount = Integer.toString(taskCount);
                 }
                 catch(NullPointerException e){
                     numTasks.setText(0);
                     Log.e("Task Count", "Error");
                 }

                 final String stringtaskCount = Integer.toString(taskCount);
                 Log.e("This is task:", stringtaskCount);
                 Log.e("This is userName:", currentName);
                 name.setText(currentName);
                 organization.setText(organizationName);

                 Picasso.get().load(liLink).into(proPic);

            }
        });



    }
}
