package com.mattfein.iamcp;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvocateDashboard extends Fragment {

    CardView repCard, issueCard, advocateCard;
    ConstraintLayout advocateIncomplete, advocateComplete, policyIncomplete, policyComplete, repIncomplete, repComplete;
    Intent advocateActivity, issueActivity, repActivity;
    Boolean advocateRead, repRead, issueRead;
    RelativeLayout layout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String REPCOUNT = "isRepRead";
    private static final String POLICYCOUNT = "isPolicyRead";
    private static final String ADVOCACYCOUNT = "isAdvocacyRead";



    public AdvocateDashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        advocateRead = false;
        repRead = false;
        issueRead = false;

        View v = inflater.inflate(R.layout.fragment_advocte_dashboard, container, false);
        repCard = v.findViewById(R.id.representativesCard);
        issueCard = v.findViewById(R.id.issueCard);
        advocateCard = v.findViewById(R.id.advocateCard);
        advocateIncomplete = v.findViewById(R.id.advocateIncomplete);
        advocateComplete = v.findViewById(R.id.advocateComplete);

        repIncomplete = v.findViewById(R.id.repIncomplete);
        repComplete = v.findViewById(R.id.repComplete);
        policyIncomplete = v.findViewById(R.id.polIncomplete);
        policyComplete = v.findViewById(R.id.polComplete);
        setUpClickListeners(repCard, issueCard, advocateCard);
        issueActivity = new Intent(getActivity(), issueActivity.class);
        advocateActivity = new Intent(getActivity(), advocateActivity.class);
        repActivity = new Intent(getActivity(), repActivity.class);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fbUser = mAuth.getCurrentUser();
        final String fbUserEmail = fbUser.getEmail();

        checkCompletion(policyComplete, policyIncomplete, repIncomplete, repComplete, advocateComplete, advocateIncomplete, fbUserEmail);



        return v;
    }

    private void checkCompletion(final ConstraintLayout policyComplete, final ConstraintLayout policyIncomplete, final ConstraintLayout repIncomplete, final ConstraintLayout repComplete, final ConstraintLayout advocateComplete, final ConstraintLayout advocateIncomplete, String userEmail) {
        final DocumentReference query = db.collection("Task").document(userEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long currentAdStatus = (Long) documentSnapshot.get(ADVOCACYCOUNT);
                Long currentPolStatus = (Long) documentSnapshot.get(POLICYCOUNT);
                Long currentRepStatus = (Long) documentSnapshot.get(REPCOUNT);
                if(currentAdStatus >= 4){
                    advocateIncomplete.setVisibility(View.GONE);
                    advocateComplete.setVisibility(View.VISIBLE);
                }
                if(currentPolStatus >= 4){
                    policyIncomplete.setVisibility(View.GONE);
                    policyComplete.setVisibility(View.VISIBLE);
                }
                if(currentRepStatus >= 4){
                    repIncomplete.setVisibility(View.GONE);
                    repComplete.setVisibility(View.VISIBLE);
                }

            }
        });






    }

    private void setUpClickListeners(CardView repCard, CardView issueCard, CardView advocateCard) {

        //RepCard Click Listener
        repCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(repActivity);
            }
        });

        //IssueCard Click Listener
        issueCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issueActivity = new Intent(getActivity(), issueActivity.class);
                startActivity(issueActivity);
            }
        });

        //AdvocateCard Click Listener
        advocateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(advocateActivity);

            }
        });

    }

}
