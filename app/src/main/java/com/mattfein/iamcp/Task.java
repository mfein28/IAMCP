package com.mattfein.iamcp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task extends AppCompatActivity {

    private static final String TAG = "Task";

    FirebaseAuth mAuth;


    Spinner activityTypeSpin, issueAreaSpin;
    ArrayAdapter<CharSequence> activityTypeAdap, issueAreaAdap;
    EditText taskDescriptionBox;

    //User Specific
    private static final String ISSUE_AREA = "issueArea";
    private static final String ACTIVITY_TYPE = "activityType";
    private static final String ADVOCATE_USER = "advocateUser";
    private static final String POINT_VALUE = "pointValue";
    private static final String DESCRIPTION_ARRAY = "taskDescription";
    private static final String USER_FULL_NAME = "Name";
    private static final String USER_LI_LINK = "linkedinPro";

    //NewsFeed
    private static final String ISSUE_AREA_News = "IssueAreas";
    private static final String Names_News = "Names";
    private static final String LI_URL_NEWS = "LIUrls";
    private static final String ActivityDescriptions_NEWS = "ActivityDescriptions";

    private Boolean isOther = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final EditText otherIssue = (EditText) findViewById(R.id.otherissue);
        setSupportActionBar(toolbar);
        Intent task = getIntent();
        Window window = getWindow();
        setUpSpinners(otherIssue);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        Button submitButton = (Button) findViewById(R.id.submitButton);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fbUser = mAuth.getCurrentUser();

        final String fbUserEmail = fbUser.getEmail();
        Log.e("Emaill", fbUserEmail);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask(fbUserEmail, otherIssue);
            }
        });


    }

    private void saveTask(final String fbUser, EditText otherText) {
        String issueArea = null;
        String taskDescription = null;
        if (isOther == true) {
            String issueCheck = otherText.getText().toString();
            if (issueCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter an issue area", Toast.LENGTH_SHORT).show();
            } else {
                issueArea = otherText.getText().toString();
            }
        } else {
            issueArea = issueAreaSpin.getSelectedItem().toString();
            taskDescription = taskDescriptionBox.getText().toString();
        }

        final String activityType = activityTypeSpin.getSelectedItem().toString();
        Log.e("email", fbUser);
        Integer pointVal = 10;


        CollectionReference allUsers = db.collection("Task");
        final String finalIssueArea = issueArea;
        final String finalTaskDescription = taskDescription;
        final String finalEmail = fbUser;
        allUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Boolean shouldCreate;
                    int shouldCreCheck = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        String email = document.getString(ADVOCATE_USER);
                        if (fbUser.equals(finalEmail)) {
                            shouldCreCheck = 1;
                            Log.d("TAG", "Username found");
                            getCurrentArray(fbUser, finalIssueArea, activityType, finalTaskDescription);
                            db.collection("Task").document(fbUser).update(POINT_VALUE, 30)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Task.this, "Task Submitted", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Task.this, "Task Not Submitted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                    //User not found, create new user
                    if (shouldCreCheck == 0) {
                        Log.e("We got here", "usernamenot found");
                        Toast.makeText(Task.this, "UserName Not Found!", Toast.LENGTH_SHORT).show();
                        final Map<String, Object> taskMap = new HashMap<>();

                        //Add description here
                        List<String> issueAreaList = new ArrayList<>();
                        List<String> activityTypeList = new ArrayList<>();
                        List<String> activityDescription = new ArrayList<>();
                        issueAreaList.add(finalIssueArea);
                        activityTypeList.add(activityType);
                        activityDescription.add(finalTaskDescription);

                        taskMap.put(ADVOCATE_USER, fbUser);
                        taskMap.put(ISSUE_AREA, issueAreaList);
                        taskMap.put(DESCRIPTION_ARRAY, activityDescription);
                        taskMap.put(ACTIVITY_TYPE, activityTypeList);
                        taskMap.put(POINT_VALUE, 10);
                        db.collection("Task").document(fbUser).set(taskMap);
                        final DocumentReference query = db.collection("Task").document(fbUser);
                        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String liLink = documentSnapshot.getString("linkedinPro");
                                String currentName = documentSnapshot.getString("Name");
                                setNewsArray(currentName, finalIssueArea, activityType, liLink);
                            }

                        });
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            }
        });
    }


    private void setNewsArray(final String Name, final String finalIssueArea, final String activityType, final String LIURL) {
        DocumentReference query = db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5");
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> currentNameArray = new ArrayList<>();
                List<String> currentLIArray = new ArrayList<>();
                List<String> currentActivityArray = new ArrayList<>();
                List<String> currentIssueArray = new ArrayList<>();
                currentIssueArray = (List<String>) documentSnapshot.get(ISSUE_AREA_News);
                currentLIArray = (List<String>) documentSnapshot.get(LI_URL_NEWS);
                currentActivityArray = (List<String>) documentSnapshot.get(ActivityDescriptions_NEWS);
                currentNameArray = (List<String>) documentSnapshot.get(Names_News);

                currentActivityArray.add(activityType);
                currentNameArray.add(Name);
                currentLIArray.add(LIURL);
                currentIssueArray.add(finalIssueArea);

                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(ISSUE_AREA_News, currentIssueArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(ActivityDescriptions_NEWS, currentActivityArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(Names_News, currentNameArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(LI_URL_NEWS, currentLIArray);


            }

        });

    }

    private void getCurrentArray(final String userEmail, final String newIssueArea, final String newActivityType, final String extraDetails) {
        final DocumentReference query = db.collection("Task").document(userEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> newIssueArray = new ArrayList<>();
                List<String> newActivityArray = new ArrayList<>();
                List<String> newExtraDetailsArray = new ArrayList<>();
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get(ACTIVITY_TYPE).equals("new")) {

                        newExtraDetailsArray.add(extraDetails);
                        newActivityArray.add(newActivityType);
                        newIssueArray.add(newIssueArea);


                    } else {
                        newIssueArray = (List<String>) documentSnapshot.get(ISSUE_AREA);
                        newActivityArray = (List<String>) documentSnapshot.get(ACTIVITY_TYPE);
                        newExtraDetailsArray = (List<String>) documentSnapshot.get(DESCRIPTION_ARRAY);
                        newIssueArray.add(newIssueArea);
                        newActivityArray.add(newActivityType);
                        newExtraDetailsArray.add(extraDetails);
                        newExtraDetailsArray.add(extraDetails);
                    }

                    db.collection("Task").document(userEmail).update(ACTIVITY_TYPE, newActivityArray);
                    db.collection("Task").document(userEmail).update(ISSUE_AREA, newIssueArray);
                    db.collection("Task").document(userEmail).update(DESCRIPTION_ARRAY, newExtraDetailsArray);

                    String currentName = documentSnapshot.getString("Name");
                    String liLink = documentSnapshot.getString("linkedinPro");
                    setNewsArray(currentName, newIssueArea, newActivityType, liLink);

                }
            }
        });
    }

    private void setUpSpinners(final EditText otherIssue) {
        activityTypeSpin = (Spinner) findViewById(R.id.activitytype);
        issueAreaSpin = (Spinner) findViewById(R.id.issueareaspinner);
        taskDescriptionBox = (EditText) findViewById(R.id.taskDescription);
        activityTypeAdap = ArrayAdapter.createFromResource(this, R.array.activityType, android.R.layout.simple_spinner_item);
        activityTypeAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issueAreaAdap = ArrayAdapter.createFromResource(this, R.array.issuelist, android.R.layout.simple_spinner_item);
        issueAreaSpin.setAdapter(issueAreaAdap);
        issueAreaAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityTypeSpin.setAdapter(activityTypeAdap);
        issueAreaSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (issueAreaSpin.getSelectedItem().equals("Other")) {
                    otherIssue.setVisibility(View.VISIBLE);
                    isOther = true;
                }
                if (!issueAreaSpin.getSelectedItem().equals("Other")) {
                    isOther = false;
                    otherIssue.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

}


