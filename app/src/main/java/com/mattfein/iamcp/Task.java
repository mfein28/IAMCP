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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private static final String TIMESTAMP = "TimeStamps";
    private static final String ISSUE_AREA_News = "IssueAreas";
    private static final String Names_News = "Names";
    private static final String EXTRADETAILS = "extraDetails";
    private static final String USEREXTRA = "taskDescription";
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
        taskDescriptionBox = (EditText) findViewById(R.id.taskDescription);
        setSupportActionBar(toolbar);
        Intent task = getIntent();
        Window window = getWindow();
        setUpSpinners(otherIssue);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        Button submitButton = (Button) findViewById(R.id.submitButton);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fbUser = mAuth.getCurrentUser();


        final String fbUserEmail = fbUser.getEmail();
        Log.e("Email", fbUserEmail);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask(fbUserEmail, otherIssue);
            }
        });


    }

    private void saveTask(final String fbUser, EditText otherText) {
        Date date = new Date();
        final String stringDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
        String issueArea = null;
        String extraDetails = null;
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
        final String finalextraDetails = extraDetails;
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
                            DocumentReference user = db.collection("Task").document(finalEmail);
                            user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (activityType.equals("Emailed a lawmaker")){
                                        Long CurrentPoints = (Long) documentSnapshot.get(POINT_VALUE);
                                        if(CurrentPoints == null){
                                            CurrentPoints = (long) 10;

                                        }
                                        else{
                                            CurrentPoints = CurrentPoints + 10;
                                        }
                                        Log.e("CurrentPoints", Long.toString(CurrentPoints) );
                                        db.collection("Task").document(fbUser).update(POINT_VALUE, CurrentPoints)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Task.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Task.this, "Report Not Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    if(activityType.equals("Called a lawmaker")){
                                        Long CurrentPoints = (Long) documentSnapshot.get(POINT_VALUE);
                                        CurrentPoints = CurrentPoints + 20;
                                        db.collection("Task").document(fbUser).update(POINT_VALUE, CurrentPoints)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Task.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Task.this, "Report Not Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    if(activityType.equals("Met with a lawmaker")){
                                        Long CurrentPoints = (Long) documentSnapshot.get(POINT_VALUE);
                                        CurrentPoints = CurrentPoints + 30;
                                        db.collection("Task").document(fbUser).update(POINT_VALUE, CurrentPoints)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Task.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Task.this, "Report Not Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    if(activityType.equals("Wrote an Op-Ed")){
                                        Long CurrentPoints = (Long) documentSnapshot.get(POINT_VALUE);
                                        CurrentPoints = CurrentPoints + 40;
                                        db.collection("Task").document(fbUser).update(POINT_VALUE, CurrentPoints)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Task.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Task.this, "Report Not Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    if(activityType.equals("Attended an event")){
                                        Long CurrentPoints = (Long) documentSnapshot.get(POINT_VALUE);
                                        CurrentPoints = CurrentPoints + 10;
                                        db.collection("Task").document(fbUser).update(POINT_VALUE, CurrentPoints)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Task.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Task.this, "Report Not Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    if(activityType.equals("Raised money")){
                                        Long CurrentPoints = (Long) documentSnapshot.get(POINT_VALUE);
                                        CurrentPoints = CurrentPoints + 30;
                                        db.collection("Task").document(fbUser).update(POINT_VALUE, CurrentPoints)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Task.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Task.this, "Report Not Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                            shouldCreCheck = 1;
                            Log.d("TAG", "Username found");
                            getCurrentArray(fbUser, finalIssueArea, activityType, finalTaskDescription);

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
                                setNewsArray(currentName, finalIssueArea, activityType, extraDetails, liLink);
                            }

                        });
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            }
        });
    }


    private void setNewsArray(final String Name, final String finalIssueArea, final String activityType, final String activityDescription, final String LIURL) {
        Date date = new Date();
        final String stringDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
        DocumentReference query = db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5");
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> currentNameArray = new ArrayList<>();
                List<String> currentLIArray = new ArrayList<>();
                List<String> currentActivityArray = new ArrayList<>();
                List<String> currentIssueArray = new ArrayList<>();
                List<String> currentExtraArray = new ArrayList<>();
                List<String> currentTimeStampArray = new ArrayList<>();


                currentIssueArray = (List<String>) documentSnapshot.get(ISSUE_AREA_News);
                currentLIArray = (List<String>) documentSnapshot.get(LI_URL_NEWS);
                currentActivityArray = (List<String>) documentSnapshot.get(ActivityDescriptions_NEWS);
                currentNameArray = (List<String>) documentSnapshot.get(Names_News);
                currentExtraArray = (List<String>) documentSnapshot.get(EXTRADETAILS);
                currentTimeStampArray = (List<String>) documentSnapshot.get(TIMESTAMP);

                currentActivityArray.add(activityType);
                currentNameArray.add(Name);
                currentLIArray.add(LIURL);
                currentExtraArray.add(activityDescription);
                currentIssueArray.add(finalIssueArea);

                currentTimeStampArray.add(stringDate);

                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(ISSUE_AREA_News, currentIssueArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(ActivityDescriptions_NEWS, currentActivityArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(EXTRADETAILS, currentExtraArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(Names_News, currentNameArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(LI_URL_NEWS, currentLIArray);
                db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5").update(TIMESTAMP, currentTimeStampArray);


            }

        });

    }

    private void getCurrentArray(final String userEmail, final String newIssueArea, final String newActivityType, final String newExtraDetails) {
        Date date = new Date();
        final String stringDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
        final DocumentReference query = db.collection("Task").document(userEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> newIssueArray = new ArrayList<>();
                List<String> newActivityArray = new ArrayList<>();
                List<String> newExtraDetailsArray = new ArrayList<>();
                List<String> newDatesArray = new ArrayList<>();


                if (documentSnapshot.exists()) {

                    newIssueArray = (List<String>) documentSnapshot.get(ISSUE_AREA);
                    newActivityArray = (List<String>) documentSnapshot.get(ACTIVITY_TYPE);
                    newExtraDetailsArray = (List<String>) documentSnapshot.get(DESCRIPTION_ARRAY);
                    newDatesArray = (List<String>) documentSnapshot.get(TIMESTAMP);
                    newIssueArray.add(newIssueArea);
                    newActivityArray.add(newActivityType);
                    newDatesArray.add(stringDate);
                    newExtraDetailsArray.add(newExtraDetails);

                    db.collection("Task").document(userEmail).update(ACTIVITY_TYPE, newActivityArray);
                    db.collection("Task").document(userEmail).update(ISSUE_AREA, newIssueArray);
                    db.collection("Task").document(userEmail).update(USEREXTRA, newExtraDetailsArray);
                    db.collection("Task").document(userEmail).update(DESCRIPTION_ARRAY, newExtraDetailsArray);
                    db.collection("Task").document(userEmail).update(TIMESTAMP, newDatesArray);

                    String currentName = documentSnapshot.getString("Name");
                    String liLink = documentSnapshot.getString("linkedinPro");
                    setNewsArray(currentName, newIssueArea, newActivityType, newExtraDetails, liLink);

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
                    issueAreaSpin.setVisibility(View.GONE);
                    isOther = true;
                }
                if (!issueAreaSpin.getSelectedItem().equals("Other")) {
                    isOther = false;
                    otherIssue.setVisibility(View.GONE);
                    issueAreaSpin.setVisibility(View.VISIBLE);
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


